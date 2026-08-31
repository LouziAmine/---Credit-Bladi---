import io
import json
import os
import re
import sys
import time
import traceback
import uuid
import zipfile

import requests

BASE = os.environ.get("E2E_BASE_URL", "http://localhost:8080")
MAILPIT = os.environ.get("E2E_MAILPIT_URL", "http://localhost:8025")
STAFF_PASSWORD = "E2e!Passw0rd2026"
CLIENT_PASSWORD = "Client!Passw0rd2026"
RUN = uuid.uuid4().hex[:6]

RESULTS = []
_current_journey = ["-"]

class Failed(Exception):
    pass

class KnownGap(Exception):
    pass

def journey(name):
    _current_journey[0] = name
    print(f"\n=== {name}")

def scenario(sid, title):
    def wrap(fn):
        def run(*args, **kwargs):
            start = time.time()
            try:
                out = fn(*args, **kwargs)
                RESULTS.append((_current_journey[0], sid, title, "PASS", ""))
                print(f"  PASS {sid} {title}")
                return out
            except KnownGap as e:
                RESULTS.append((_current_journey[0], sid, title, "KNOWN_GAP", str(e)))
                print(f"  GAP  {sid} {title}\n       -> {e}")
            except Failed as e:
                RESULTS.append((_current_journey[0], sid, title, "FAIL", str(e)))
                print(f"  FAIL {sid} {title}\n       -> {e}")
            except Exception as e:
                RESULTS.append((_current_journey[0], sid, title, "ERROR", repr(e)))
                print(f"  ERROR {sid} {title}\n       -> {e!r}")
                traceback.print_exc(limit=2)
            finally:
                del start
            return None
        run.__name__ = fn.__name__
        return run
    return wrap

class Api:

    def __init__(self, label="anonymous", lang="en"):
        self.label = label
        self.s = requests.Session()
        self.s.headers.update({"Accept-Language": lang, "x-request-id": f"e2e-{RUN}-{label}"})

    def call(self, method, path, expect=None, **kw):
        r = self.s.request(method, BASE + path, timeout=30, **kw)
        if expect is not None:
            expected = expect if isinstance(expect, (list, tuple)) else [expect]
            if r.status_code not in expected:
                raise Failed(f"{self.label} {method} {path} -> {r.status_code} (expected {expect}) body={r.text[:400]}")
        return r

    def get(self, path, expect=200, **kw):
        return self.call("GET", path, expect, **kw)

    def post(self, path, body=None, expect=200, **kw):
        return self.call("POST", path, expect, json=body, **kw)

    def patch(self, path, body=None, expect=200, **kw):
        return self.call("PATCH", path, expect, json=body, **kw)

    def delete(self, path, expect=(200, 204), **kw):
        return self.call("DELETE", path, expect, **kw)

    def login(self, username, password):
        self.post("/api/v1/auth/login", {"username": username, "password": password})
        self.label = username
        return self

def check(cond, message):
    if not cond:
        raise Failed(message)

def error_code(r):
    try:
        return r.json().get("errorCode")
    except ValueError:
        return None

def expect_error(r, status, description_contains=None):
    check(r.status_code == status, f"expected HTTP {status}, got {r.status_code}: {r.text[:300]}")
    if description_contains:
        desc = r.json().get("description", "")
        check(description_contains.lower() in desc.lower(),
              f"expected description containing '{description_contains}', got '{desc}'")

def pdf_bytes(text="Credit Bladi E2E"):
    body = f"BT /F1 12 Tf 72 720 Td ({text}) Tj ET".encode()
    objs = [
        b"<< /Type /Catalog /Pages 2 0 R >>",
        b"<< /Type /Pages /Kids [3 0 R] /Count 1 >>",
        b"<< /Type /Page /Parent 2 0 R /MediaBox [0 0 612 792] /Contents 4 0 R >>",
        b"<< /Length " + str(len(body)).encode() + b" >>\nstream\n" + body + b"\nendstream",
    ]
    out = io.BytesIO()
    out.write(b"%PDF-1.4\n")
    offsets = []
    for i, o in enumerate(objs, 1):
        offsets.append(out.tell())
        out.write(f"{i} 0 obj\n".encode() + o + b"\nendobj\n")
    xref = out.tell()
    out.write(f"xref\n0 {len(objs) + 1}\n0000000000 65535 f \n".encode())
    for off in offsets:
        out.write(f"{off:010d} 00000 n \n".encode())
    out.write(f"trailer << /Size {len(objs) + 1} /Root 1 0 R >>\nstartxref\n{xref}\n%%EOF\n".encode())
    return out.getvalue()

EICAR = rb"X5O!P%@AP[4\PZX54(P^)7CC)7}$EICAR-STANDARD-ANTIVIRUS-TEST-FILE!$H+H*"

def infected_docx_bytes():
    out = io.BytesIO()
    with zipfile.ZipFile(out, "w") as z:
        z.writestr("[Content_Types].xml",
                   '<?xml version="1.0" encoding="UTF-8"?><Types xmlns="http://schemas.openxmlformats.org/'
                   'package/2006/content-types"><Default Extension="xml" ContentType="application/xml"/>'
                   '<Override PartName="/word/document.xml" ContentType="application/vnd.openxmlformats-'
                   'officedocument.wordprocessingml.document.main+xml"/></Types>')
        z.writestr("_rels/.rels",
                   '<?xml version="1.0" encoding="UTF-8"?><Relationships xmlns="http://schemas.openxmlformats.org/'
                   'package/2006/relationships"><Relationship Id="rId1" Type="http://schemas.openxmlformats.org/'
                   'officeDocument/2006/relationships/officeDocument" Target="word/document.xml"/></Relationships>')
        z.writestr("word/document.xml",
                   '<?xml version="1.0" encoding="UTF-8"?><w:document xmlns:w="http://schemas.openxmlformats.org/'
                   'wordprocessingml/2006/main"><w:body><w:p><w:r><w:t>E2E</w:t></w:r></w:p></w:body></w:document>')
        z.writestr("eicar.com", EICAR)
    return out.getvalue()

def mailpit_messages(to_email):
    r = requests.get(f"{MAILPIT}/api/v1/search", params={"query": f"to:{to_email}"}, timeout=10)
    r.raise_for_status()
    return r.json().get("messages", [])

def mailpit_body(message_id):
    r = requests.get(f"{MAILPIT}/api/v1/message/{message_id}", timeout=10)
    r.raise_for_status()
    data = r.json()
    return (data.get("Text") or "") + "\n" + (data.get("HTML") or "")

def wait_for_mail(to_email, pattern, timeout=15):
    deadline = time.time() + timeout
    while time.time() < deadline:
        for m in mailpit_messages(to_email):
            body = mailpit_body(m["ID"])
            found = re.search(pattern, body)
            if found:
                return found
        time.sleep(0.5)
    raise Failed(f"no mail to {to_email} matching {pattern} within {timeout}s")

def report(path):
    total = len(RESULTS)
    passed = sum(1 for r in RESULTS if r[3] in ("PASS", "KNOWN_GAP"))
    with open(path, "w") as f:
        json.dump([dict(zip(("journey", "id", "title", "status", "detail"), r)) for r in RESULTS], f, indent=2,
                  ensure_ascii=False)
    gaps = sum(1 for r in RESULTS if r[3] == "KNOWN_GAP")
    print(f"\n{passed - gaps}/{total} scenarios passed, {gaps} known gap(s)")
    for r in RESULTS:
        if r[3] != "PASS":
            print(f"  {r[3]} {r[1]} {r[2]}: {r[4][:300]}")
    return passed == total

def main_guard(fn):
    try:
        fn()
    finally:
        ok = report(os.environ.get("E2E_REPORT", "e2e-report.json"))
        sys.exit(0 if ok else 1)
