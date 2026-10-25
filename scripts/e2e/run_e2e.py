from lib import (Api, Failed, KnownGap, RUN, STAFF_PASSWORD, CLIENT_PASSWORD, check, error_code, expect_error, journey,
                 main_guard, pdf_bytes, infected_docx_bytes, scenario, wait_for_mail)

ADMIN = Api("admin")
PUBLIC = Api("public")
STAFF = {}
SESSIONS = {}
CTX = {}

RIB_VALID = "230 780 0123456789012345 03"
RIB_OTHER = "230 780 0000000000000000 39"
RIB_BAD_KEY = "230 780 0123456789012345 95"
KEEP_ALL = {"note": "x", "rateOption": "KEEP", "amountOption": "KEEP", "durationOption": "KEEP"}

def as_user(username):
    if username not in SESSIONS:
        SESSIONS[username] = Api(username).login(username, STAFF_PASSWORD)
    return SESSIONS[username]

def demand_by_reference(reference):
    found, page = [], 1
    while not found:
        data = ADMIN.get(f"/api/v1/demands?page={page}&size=50").json()
        found = [d for d in data["items"] if d["reference"] == reference]
        if page >= (data.get("lastPage") or 1):
            break
        page += 1
    check(found, f"demand {reference} not found in the back-office list")
    return found[0]

def pipeline(demand_id):
    return ADMIN.get(f"/api/v1/demands/{demand_id}/pipeline").json()

def current_step(demand_id):
    return ADMIN.get(f"/api/v1/demands/{demand_id}/current-step").json()

def staff_for(grade, team, index=0):
    if grade is None and team is None:
        return STAFF[("ANY", "ANY")][index]
    return STAFF[(grade, team)][index]

def mortgage_body(**over):
    body = {"applicantName": f"Karim Benali {RUN}", "email": f"karim.{RUN}@example.ma", "phone": "0612345678",
            "amount": 500000, "duration": 240, "cspCode": "SALA", "nationalityCode": "MA",
            "moroccanResident": True, "propertyValue": 600000, "monthlyIncome": 20000, "age": 35}
    body.update(over)
    return body

def investment_body(**over):
    body = {"companyName": f"Atlas Textile {RUN}", "enterpriseType": "PME", "sector": "Textile",
            "email": f"contact.{RUN}@atlas.ma", "phone": "0612345678", "amount": 1500000, "duration": 84,
            "annualRevenue": 8000000, "companyAgeYears": 5, "existingAnnualDebtService": 100000,
            "netAnnualResult": 1200000, "guaranteesValue": 1300000}
    body.update(over)
    return body

@scenario("S-01", "Admin logs in and creates staff for every grade/team of the three workflows (x2 for four-eyes)")
def setup_staff():
    ADMIN.login("admin", "admin123")
    pairs = set()
    probe = PUBLIC.post("/api/v1/investment-credit", investment_body(companyName=f"Probe {RUN}",
                                                                      email=f"probe.{RUN}@x.ma")).json()
    probe_ge = PUBLIC.post("/api/v1/investment-credit", investment_body(companyName=f"Probe GE {RUN}",
                                                                         enterpriseType="GE",
                                                                         email=f"probe-ge.{RUN}@x.ma")).json()
    probe_m = PUBLIC.post("/api/v1/mortgage-credit", mortgage_body(email=f"probe-m.{RUN}@x.ma",
                                                                     applicantName=f"Probe M {RUN}")).json()
    for ref in (probe["demandReference"], probe_ge["demandReference"], probe_m["demandReference"]):
        for step in pipeline(demand_by_reference(ref)["id"]):
            pairs.add((step["requiredGradeCode"], step["requiredTeamCode"]))
    pairs.add(("ANY", "ANY"))
    for grade, team in sorted(pairs, key=str):
        users = []
        for i in (1, 2):
            username = f"e2e.{(grade or 'nog').lower()}.{(team or 'not').lower()}.{i}.{RUN}"[:50]
            body = {"username": username, "fullName": f"E2E {grade}/{team} {i}", "email": f"{username}@e2e.ma",
                    "password": STAFF_PASSWORD, "role": "MANAGER"}
            if grade not in (None, "ANY"):
                body["gradeCode"] = grade
            if team not in (None, "ANY"):
                body["teamCode"] = team
            ADMIN.post("/api/v1/users", body, expect=201)
            users.append(username)
        STAFF[(grade, team)] = users
    CTX["probe_refs"] = (probe["demandReference"], probe_ge["demandReference"], probe_m["demandReference"])
    check(len(STAFF) >= 10, f"only {len(STAFF)} grade/team pairs found")

@scenario("A-01", "Conventional mortgage, eligible -> 200, dossier created at CLASSIFICATION_STEP")
def a01():
    r = PUBLIC.post("/api/v1/mortgage-credit", mortgage_body()).json()
    check(r["demandReference"].startswith("DEM-"), "missing demandReference")
    check(r["productType"] == "CONVENTIONAL" and r["taeg"] is not None, "offer incomplete")
    check(len(r["amortizationSchedule"]) == 240, "schedule should have 240 rows")
    CTX["mortgage_ref"] = r["demandReference"]
    d = demand_by_reference(r["demandReference"])
    check(current_step(d["id"])["stepCode"] == "CLASSIFICATION_STEP", "should start at CLASSIFICATION_STEP")

@scenario("A-02", "Same person simulates again before classification -> same dossier refreshed")
def a02():
    r = PUBLIC.post("/api/v1/mortgage-credit", mortgage_body(amount=450000)).json()
    check(r["demandReference"] == CTX["mortgage_ref"], "a second dossier was created instead of refreshing")
    check(float(demand_by_reference(r["demandReference"])["amount"]) == 450000, "terms not refreshed")

@scenario("A-03", "Duration outside the CSP range (SALA 300 months) -> 422 DURATION_LIMIT_EXCEEDED, no dossier")
def a03():
    before = ADMIN.get("/api/v1/demands?page=1&size=1").json()["total"]
    r = PUBLIC.post("/api/v1/mortgage-credit", mortgage_body(duration=300, email=f"a03.{RUN}@x.ma"), expect=422)
    after = ADMIN.get("/api/v1/demands?page=1&size=1").json()["total"]
    check(before == after, "a rejected simulation created a dossier")
    del r

@scenario("A-04", "Deposit insufficient (loan > LTV) -> 422")
def a04():
    PUBLIC.post("/api/v1/mortgage-credit", mortgage_body(amount=590000, email=f"a04.{RUN}@x.ma"), expect=422)

@scenario("A-05", "Too old at maturity -> 422")
def a05():
    PUBLIC.post("/api/v1/mortgage-credit", mortgage_body(age=65, email=f"a05.{RUN}@x.ma"), expect=422)

@scenario("A-06", "Debt ratio exceeded -> 422")
def a06():
    PUBLIC.post("/api/v1/mortgage-credit", mortgage_body(monthlyIncome=3000, email=f"a06.{RUN}@x.ma"), expect=422)

@scenario("A-07", "Mourabaha eligible -> 200 with TTC schedule")
def a07():
    r = PUBLIC.post("/api/v1/mortgage-credit", mortgage_body(productType="MOURABAHA", email=f"mourabaha.{RUN}@x.ma",
                                                              applicantName=f"Mourabaha {RUN}")).json()
    check(r["productType"] == "MOURABAHA" and r["mourabaha"] is not None, "Mourabaha offer missing")
    CTX["mourabaha_ref"] = r["demandReference"]

@scenario("A-08", "Validation: missing fields / bad nationality shape / unknown ISO country -> 400 with precise message")
def a08():
    PUBLIC.post("/api/v1/mortgage-credit", {"amount": 1}, expect=400)
    expect_error(PUBLIC.post("/api/v1/mortgage-credit", mortgage_body(nationalityCode="M"), expect=None), 400,
                 "format")
    expect_error(PUBLIC.post("/api/v1/mortgage-credit", mortgage_body(nationalityCode="ZZ"), expect=None), 400,
                 "not a recognized")
    PUBLIC.post("/api/v1/mortgage-credit", mortgage_body(cspCode="NOPE"), expect=400)

@scenario("A-09", "Error messages follow Accept-Language (fr/ar)")
def a09():
    fr = Api("public-fr", lang="fr").post("/api/v1/mortgage-credit", mortgage_body(nationalityCode="ZZ"), expect=400)
    ar = Api("public-ar", lang="ar").post("/api/v1/mortgage-credit", mortgage_body(nationalityCode="ZZ"), expect=400)
    check("pays" in fr.json()["description"], f"not French: {fr.json()['description']}")
    check(any("؀" <= ch <= "ۿ" for ch in ar.json()["description"]), "not Arabic")

@scenario("B-01", "PME eligible -> 200, dossier on PME_INVESTMENT workflow")
def b01():
    r = PUBLIC.post("/api/v1/investment-credit", investment_body()).json()
    check(r["demandReference"].startswith("DEM-") and len(r["amortizationSchedule"]) == 84, "offer incomplete")
    CTX["pme_ref"] = r["demandReference"]
    steps = [s["requiredTeamCode"] for s in pipeline(demand_by_reference(r["demandReference"])["id"])]
    check("PPIF_PME" in steps, f"PME dossier not on the PME workflow: {steps}")

@scenario("B-02", "GE eligible -> dossier on GE_INVESTMENT workflow (PPIF_GE teams)")
def b02():
    r = PUBLIC.post("/api/v1/investment-credit", investment_body(enterpriseType="GE", companyName=f"Holding {RUN}",
                                                                 email=f"ge.{RUN}@x.ma", amount=3000000,
                                                                 guaranteesValue=3000000, netAnnualResult=3000000)).json()
    steps = [s["requiredTeamCode"] for s in pipeline(demand_by_reference(r["demandReference"])["id"])]
    check("PPIF_GE" in steps, f"GE dossier not on the GE workflow: {steps}")
    CTX["ge_ref"] = r["demandReference"]

@scenario("B-03", "Company too young -> 422")
def b03():
    PUBLIC.post("/api/v1/investment-credit", investment_body(companyAgeYears=0, email=f"b03.{RUN}@x.ma"), expect=422)

@scenario("B-04", "Amount / revenue above financing ratio -> 422")
def b04():
    PUBLIC.post("/api/v1/investment-credit", investment_body(annualRevenue=1000000, email=f"b04.{RUN}@x.ma"),
                expect=422)

@scenario("B-05", "Guarantees below required coverage -> 422")
def b05():
    PUBLIC.post("/api/v1/investment-credit", investment_body(guaranteesValue=1000, email=f"b05.{RUN}@x.ma"),
                expect=422)

@scenario("B-06", "Debt service ratio exceeded -> 422")
def b06():
    PUBLIC.post("/api/v1/investment-credit", investment_body(netAnnualResult=100000, email=f"b06.{RUN}@x.ma"),
                expect=422)

@scenario("B-07", "Same company + email simulates again before classification -> same reference")
def b07():
    r = PUBLIC.post("/api/v1/investment-credit", investment_body(amount=1400000)).json()
    check(r["demandReference"] == CTX["pme_ref"], "duplicate dossier instead of refresh")

@scenario("C-01", "Client registers, logs in, /me, refresh rotation, logout revokes access")
def c01():
    client = Api("client")
    username = f"client.{RUN}"
    client.post("/api/v1/auth/register", {"username": username, "password": CLIENT_PASSWORD}, expect=(200, 201))
    client.login(username, CLIENT_PASSWORD)
    me = client.get("/api/v1/auth/me").json()
    check(me["role"] == "CLIENT", f"unexpected role {me}")
    client.post("/api/v1/auth/refresh", expect=(200, 204))
    client.get("/api/v1/auth/me")
    client.post("/api/v1/auth/logout", expect=(200, 204))
    client.get("/api/v1/auth/me", expect=401)

@scenario("C-02", "Duplicate username -> 409; weak password -> 400; wrong password -> 401 generic")
def c02():
    PUBLIC.post("/api/v1/auth/register", {"username": f"client.{RUN}", "password": CLIENT_PASSWORD}, expect=409)
    PUBLIC.post("/api/v1/auth/register", {"username": f"weak.{RUN}", "password": "abc"}, expect=400)
    PUBLIC.post("/api/v1/auth/login", {"username": f"client.{RUN}", "password": "Wrong!Passw0rd"}, expect=401)

@scenario("C-03", "Role separation: anonymous -> 401 on back-office, client -> 403 on back-office")
def c03():
    PUBLIC.get("/api/v1/demands?page=1&size=1", expect=401)
    client = Api("client").login(f"client.{RUN}", CLIENT_PASSWORD)
    client.get("/api/v1/demands?page=1&size=1", expect=403)
    client.get("/api/v1/users?page=1&size=1", expect=403)

@scenario("D-01", "Advance while at CLASSIFICATION_STEP -> 409 MUST_CLASSIFY_FIRST")
def d01():
    d = demand_by_reference(CTX["pme_ref"])
    ca = as_user(staff_for("CHARGE_AFFAIRES", "RESEAU"))
    ca.post(f"/api/v1/demands/{d['id']}/advance", KEEP_ALL, expect=409)

@scenario("D-02", "Classify REAL enterprise dossier without enterpriseId -> 422; unknown enterprise -> 404")
def d02():
    d = demand_by_reference(CTX["pme_ref"])
    ca = as_user(staff_for("CHARGE_AFFAIRES", "RESEAU"))
    ca.post(f"/api/v1/demands/{d['id']}/classify", {"classificationOutcome": "REAL", "note": "ok"}, expect=422)
    ca.post(f"/api/v1/demands/{d['id']}/classify",
            {"classificationOutcome": "REAL", "enterpriseId": "00000000-0000-0000-0000-000000000000"}, expect=404)

@scenario("D-03", "Actor without the step's grade/team cannot classify -> 403")
def d03():
    d = demand_by_reference(CTX["pme_ref"])
    analyst = as_user(staff_for("ANALYSTE", "PAE"))
    analyst.post(f"/api/v1/demands/{d['id']}/classify", {"classificationOutcome": "FAKE"}, expect=403)

@scenario("D-04", "Classify FAKE -> dossier REJECTED, further advance -> 409")
def d04():
    ref = PUBLIC.post("/api/v1/investment-credit", investment_body(companyName=f"Fake Co {RUN}",
                                                                   email=f"fake.{RUN}@x.ma")).json()["demandReference"]
    d = demand_by_reference(ref)
    ca = as_user(staff_for("CHARGE_AFFAIRES", "RESEAU"))
    ca.post(f"/api/v1/demands/{d['id']}/classify", {"classificationOutcome": "FAKE", "note": "injoignable"})
    check(demand_by_reference(ref)["rejected"], "FAKE classification should reject the dossier")
    ca.post(f"/api/v1/demands/{d['id']}/advance", KEEP_ALL, expect=409)

@scenario("D-05", "Classify REAL with an invalid RIB -> 400, nothing changed")
def d05():
    d = demand_by_reference(CTX["pme_ref"])
    ent = ADMIN.post("/api/v1/enterprises", {"name": f"Atlas Textile SARL {RUN}", "sector": "Textile",
                                              "registrationNumber": f"RC-{RUN}"}, expect=201).json()
    CTX["enterprise"] = ent
    ca = as_user(staff_for("CHARGE_AFFAIRES", "RESEAU"))
    ca.post(f"/api/v1/demands/{d['id']}/classify",
            {"classificationOutcome": "REAL", "enterpriseId": ent["id"], "clientRib": RIB_BAD_KEY,
             "assigneeUsername": staff_for("CHARGE_AFFAIRES", "RESEAU")}, expect=400)
    check(current_step(d["id"])["stepCode"] == "CLASSIFICATION_STEP", "invalid RIB still moved the dossier")

@scenario("D-06", "Classify REAL with enterprise + RIB + next assignee -> INITIATION_STEP, invitation mailed to "
                  "the contact (no client account with that email)")
def d06():
    d = demand_by_reference(CTX["pme_ref"])
    ca = as_user(staff_for("CHARGE_AFFAIRES", "RESEAU"))
    ca.post(f"/api/v1/demands/{d['id']}/classify",
            {"classificationOutcome": "REAL", "enterpriseId": CTX["enterprise"]["id"], "clientRib": RIB_VALID,
             "assigneeUsername": staff_for("CHARGE_AFFAIRES", "RESEAU", 1), "note": "client confirmé"})
    step = current_step(d["id"])
    check(step["stepCode"] == "INITIATION_STEP", f"expected INITIATION_STEP, got {step['stepCode']}")
    check(step["assignedUsername"] == staff_for("CHARGE_AFFAIRES", "RESEAU", 1), "assignee not recorded")
    token = wait_for_mail(f"contact.{RUN}@atlas.ma", r"invitations/([A-Za-z0-9_\-]+)").group(1)
    CTX["invitation_token"] = token

@scenario("G-01", "Invitation preview (public) then accept -> CLIENT account + APPROVED link, portfolio visible")
def g01():
    token = CTX["invitation_token"]
    preview = PUBLIC.get(f"/api/v1/invitations/{token}").json()
    check(CTX["enterprise"]["name"] in str(preview), f"preview does not name the enterprise: {preview}")
    username = f"invited.{RUN}"
    PUBLIC.post(f"/api/v1/invitations/{token}/accept", {"username": username, "password": CLIENT_PASSWORD},
                expect=(200, 201))
    client = Api("invited").login(username, CLIENT_PASSWORD)
    SESSIONS["invited"] = client
    portfolio = client.get("/api/v1/me/portfolio").json()
    check(any(CTX["enterprise"]["id"] in str(p) for p in (portfolio if isinstance(portfolio, list) else [portfolio])),
          f"enterprise not in portfolio: {portfolio}")
    demands = client.get(f"/api/v1/me/portfolio/{CTX['enterprise']['id']}/demands").json()
    check(CTX["pme_ref"] in str(demands), "dossier not visible in the client portfolio")

@scenario("G-02", "Invitation token cannot be reused -> 4xx")
def g02():
    r = PUBLIC.post(f"/api/v1/invitations/{CTX['invitation_token']}/accept",
                    {"username": f"again.{RUN}", "password": CLIENT_PASSWORD}, expect=None)
    check(400 <= r.status_code < 500, f"reusing an accepted invitation returned {r.status_code}")

@scenario("G-03", "Client claims an enterprise by RC number -> PENDING; manager rejects; claims again; approves; "
                  "revokes -> access removed")
def g03():
    ent = ADMIN.post("/api/v1/enterprises", {"name": f"Claimable SARL {RUN}", "sector": "Commerce",
                                              "registrationNumber": f"RC-CLAIM-{RUN}"}, expect=201).json()
    client = Api("client").login(f"client.{RUN}", CLIENT_PASSWORD)
    link = client.post("/api/v1/me/enterprise-links", {"registrationNumber": f"RC-CLAIM-{RUN}",
                                                        "role": "RESPONSABLE"}, expect=(200, 201)).json()
    check(link["status"] == "PENDING", f"claim should be PENDING: {link}")
    client.get(f"/api/v1/me/portfolio/{ent['id']}/demands", expect=(403, 404))
    ADMIN.patch(f"/api/v1/enterprise-user-links/{link['id']}/reject", {"reason": "pièces manquantes"})
    ADMIN.patch(f"/api/v1/enterprise-user-links/{link['id']}/approve", expect=(409, 422))
    link = client.post("/api/v1/me/enterprise-links", {"registrationNumber": f"RC-CLAIM-{RUN}",
                                                        "role": "RESPONSABLE"}, expect=(200, 201)).json()
    ADMIN.patch(f"/api/v1/enterprise-user-links/{link['id']}/approve")
    client.get(f"/api/v1/me/portfolio/{ent['id']}/demands")
    ADMIN.patch(f"/api/v1/enterprise-user-links/{link['id']}/revoke")
    client.get(f"/api/v1/me/portfolio/{ent['id']}/demands", expect=(403, 404))

@scenario("G-04", "IDOR: a client with no link cannot read another enterprise's dossier, documents or credit file")
def g04():
    stranger = Api("stranger")
    PUBLIC.post("/api/v1/auth/register", {"username": f"stranger.{RUN}", "password": CLIENT_PASSWORD},
                expect=(200, 201))
    stranger.login(f"stranger.{RUN}", CLIENT_PASSWORD)
    d = demand_by_reference(CTX["pme_ref"])
    stranger.get(f"/api/v1/me/portfolio/{CTX['enterprise']['id']}/demands", expect=(403, 404))
    stranger.get(f"/api/v1/me/demands/{d['id']}/document-requests", expect=(403, 404))
    stranger.get(f"/api/v1/me/demands/{d['id']}/credit-file", expect=(403, 404))
    SESSIONS["stranger"] = stranger

def advance(demand_id, actor, next_assignee, expect=200, **extra):
    body = {"note": f"e2e {RUN}", "assigneeUsername": next_assignee, "rateOption": "KEEP",
            "amountOption": "KEEP", "durationOption": "KEEP"}
    body.update(extra)
    return as_user(actor).post(f"/api/v1/demands/{demand_id}/advance", body, expect=expect)

def next_step_staff(demand_id, current_code, index=0):
    steps = pipeline(demand_id)
    codes = [s["stepCode"] for s in steps]
    i = codes.index(current_code)
    if i + 1 >= len(steps):
        return None
    nxt = steps[i + 1]
    return staff_for(nxt["requiredGradeCode"], nxt["requiredTeamCode"], index)

@scenario("F-01", "Upload a real PDF (sniffed, scanned CLEAN by ClamAV, encrypted) and download the same bytes")
def f01():
    d = demand_by_reference(CTX["pme_ref"])
    actor = as_user(current_step(d["id"])["assignedUsername"])
    content = pdf_bytes()
    doc = actor.call("POST", f"/api/v1/demands/{d['id']}/documents", expect=(200, 201),
                     files={"file": ("statuts.pdf", content, "application/pdf")},
                     data={"documentType": "Statuts"}).json()
    check(doc["scanStatus"] == "CLEAN", f"expected CLEAN scan, got {doc['scanStatus']}")
    CTX["doc_id"] = doc["id"]
    got = actor.get(f"/api/v1/demands/{d['id']}/documents/{doc['id']}/download").content
    check(got == content, "downloaded bytes differ from the uploaded file")

@scenario("F-02", "Upload a text file renamed .pdf -> 422 FILE_TYPE_NOT_ALLOWED (real type is sniffed)")
def f02():
    d = demand_by_reference(CTX["pme_ref"])
    actor = as_user(current_step(d["id"])["assignedUsername"])
    actor.call("POST", f"/api/v1/demands/{d['id']}/documents", expect=422,
               files={"file": ("fake.pdf", b"just text, not a pdf", "application/pdf")})

@scenario("F-03", "Upload a .docx carrying the EICAR test virus -> 422 FILE_INFECTED")
def f03():
    d = demand_by_reference(CTX["pme_ref"])
    actor = as_user(current_step(d["id"])["assignedUsername"])
    r = actor.call("POST", f"/api/v1/demands/{d['id']}/documents", expect=None,
                   files={"file": ("infected.docx", infected_docx_bytes(),
                                   "application/vnd.openxmlformats-officedocument.wordprocessingml.document")})
    check(r.status_code == 422, f"infected upload returned {r.status_code}: {r.text[:200]}")

@scenario("F-04", "Re-upload the same document type -> previous version SUPERSEDED")
def f04():
    d = demand_by_reference(CTX["pme_ref"])
    actor = as_user(current_step(d["id"])["assignedUsername"])
    new = actor.call("POST", f"/api/v1/demands/{d['id']}/documents", expect=(200, 201),
                     files={"file": ("statuts-v2.pdf", pdf_bytes("v2"), "application/pdf")},
                     data={"documentType": "Statuts"}).json()
    docs = {x["id"]: x for x in actor.get(f"/api/v1/demands/{d['id']}/documents").json()}
    old = docs[CTX["doc_id"]]
    check(old["status"] == "SUPERSEDED", f"old version status {old['status']}")
    check(old["supersededByDocumentId"] == new["id"], "supersededByDocumentId should be the new document's public id")

@scenario("F-05", "Document request -> client sees it, uploads, manager downloads the fulfilled document")
def f05():
    d = demand_by_reference(CTX["pme_ref"])
    actor = as_user(current_step(d["id"])["assignedUsername"])
    req = actor.post(f"/api/v1/demands/{d['id']}/document-requests", {"documentType": "Bilan 2025"},
                     expect=(200, 201)).json()
    check(req["demandId"] == d["id"], "document request demandId should be the dossier's public id")
    client = SESSIONS["invited"]
    listed = client.get(f"/api/v1/me/demands/{d['id']}/document-requests").json()
    check(any(x["id"] == req["id"] for x in listed), "client does not see the request")
    client.call("POST", f"/api/v1/me/demands/{d['id']}/document-requests/{req['id']}/documents", expect=(200, 201),
                files={"file": ("bilan.pdf", pdf_bytes("bilan"), "application/pdf")})
    reqs = {x["id"]: x for x in actor.get(f"/api/v1/demands/{d['id']}/document-requests").json()}
    check(reqs[req["id"]]["status"] == "FULFILLED", "request not fulfilled")
    client.call("POST", f"/api/v1/me/demands/{d['id']}/document-requests/{req['id']}/documents", expect=(409, 422),
                files={"file": ("bilan.pdf", pdf_bytes("bilan"), "application/pdf")})
    SESSIONS["stranger"].call("POST", f"/api/v1/me/demands/{d['id']}/document-requests/{req['id']}/documents",
                              expect=(403, 404), files={"file": ("x.pdf", pdf_bytes(), "application/pdf")})

@scenario("E-01", "Comments, history, assignment history and pipeline statuses are consistent")
def e01():
    d = demand_by_reference(CTX["pme_ref"])
    actor = as_user(current_step(d["id"])["assignedUsername"])
    actor.post(f"/api/v1/demands/{d['id']}/comments", {"comment": "Dossier complet"}, expect=(200, 201))
    check(any(c["comment"] == "Dossier complet" for c in actor.get(f"/api/v1/demands/{d['id']}/comments").json()),
          "comment not listed")
    statuses = [s["status"] for s in pipeline(d["id"])]
    check(statuses[0] == "DONE" and statuses[1] == "CURRENT" and statuses[-1] == "UPCOMING", f"pipeline {statuses}")
    check(len(actor.get(f"/api/v1/demands/{d['id']}/history").json()) >= 2, "history too short")
    actor.get(f"/api/v1/demands/{d['id']}/assignment-history")

@scenario("E-02", "Only the assignee (or same grade+team) may advance -> 403 otherwise")
def e02():
    d = demand_by_reference(CTX["pme_ref"])
    intruder = as_user(staff_for("ANALYSTE", "PAE"))
    intruder.post(f"/api/v1/demands/{d['id']}/advance", {**KEEP_ALL,
                                                          "assigneeUsername": staff_for("CHARGE_INVESTISSEMENT",
                                                                                        "PPIF_PME")}, expect=403)

@scenario("E-03", "Next assignee not eligible for the next step -> 403; missing -> 422")
def e03():
    d = demand_by_reference(CTX["pme_ref"])
    actor = current_step(d["id"])["assignedUsername"]
    advance(d["id"], actor, staff_for("ANALYSTE", "PAE"), expect=403)
    as_user(actor).post(f"/api/v1/demands/{d['id']}/advance", KEEP_ALL, expect=422)

@scenario("E-04", "Reassign: eligible-assignees CURRENT, assign to ineligible -> 403, eligible -> 200")
def e04():
    d = demand_by_reference(CTX["pme_ref"])
    step = current_step(d["id"])
    actor = as_user(step["assignedUsername"])
    names = [u["username"] for u in actor.get(f"/api/v1/demands/{d['id']}/eligible-assignees?direction=CURRENT").json()]
    check(staff_for("CHARGE_AFFAIRES", "RESEAU") in names, f"eligible list incomplete: {names}")
    actor.post(f"/api/v1/demands/{d['id']}/assign", {"username": staff_for("ANALYSTE", "PAE")}, expect=403)
    actor.post(f"/api/v1/demands/{d['id']}/assign", {"username": staff_for("CHARGE_AFFAIRES", "RESEAU")})
    check(current_step(d["id"])["assignedUsername"] == staff_for("CHARGE_AFFAIRES", "RESEAU"), "not reassigned")

@scenario("E-05", "Walk the PME pipeline to DECISION_STEP (a return for completion on the way)")
def e05():
    d = demand_by_reference(CTX["pme_ref"])
    returned = False
    for _ in range(20):
        step = current_step(d["id"])
        code = step["stepCode"]
        if code == "DECISION_STEP":
            return
        actor = step["assignedUsername"]
        if code == "INSTRUCTION_MONTAGE_STEP" and not returned:
            prev_staff = staff_for("CHARGE_INVESTISSEMENT", "PPIF_PME", 1)
            advance(d["id"], actor, prev_staff, returnReasonCode="PIECE_MANQUANTE")
            check(current_step(d["id"])["stepCode"] == "INSTRUCTION_TS_STEP", "return did not go back one step")
            returned = True
            continue
        advance(d["id"], actor, next_step_staff(d["id"], code))
    raise Failed("never reached DECISION_STEP")

@scenario("E-06", "Decision rules: DEFERRED without return -> 409, MANUAL rate above TMIC -> 422, MANUAL amount "
                  "breaking the financing ratio -> 422")
def e06():
    d = demand_by_reference(CTX["pme_ref"])
    actor = current_step(d["id"])["assignedUsername"]
    nxt = next_step_staff(d["id"], "DECISION_STEP")
    advance(d["id"], actor, nxt, expect=409, decisionOutcome="DEFERRED")
    advance(d["id"], actor, nxt, expect=422, decisionOutcome="APPROVED", rateOption="MANUAL", manualRate=15.5)
    advance(d["id"], actor, nxt, expect=422, decisionOutcome="APPROVED", amountOption="MANUAL",
            manualAmount=9000000)

@scenario("E-07", "Committee APPROVED with a MANUAL rate within limits -> CONTRACT_EDITION_STEP, change audited")
def e07():
    d = demand_by_reference(CTX["pme_ref"])
    actor = current_step(d["id"])["assignedUsername"]
    advance(d["id"], actor, next_step_staff(d["id"], "DECISION_STEP"), decisionOutcome="APPROVED",
            committeeType="ROTATING", rateOption="MANUAL", manualRate=6.25)
    check(current_step(d["id"])["stepCode"] == "CONTRACT_EDITION_STEP", "decision did not move forward")
    check(float(demand_by_reference(CTX["pme_ref"])["applicableRate"]) == 6.25, "agreed rate not frozen")
    audit = ADMIN.get(f"/api/v1/audit-log?page=1&size=50&entityType=DEMAND&entityId={d['id']}").json()["items"]
    check(any("6.25" in (a.get("details") or "") for a in audit), "rate change missing from the dossier audit trail")

@scenario("E-08", "Insurance step without ADI confirmation -> 422, with ADI -> next step")
def e08():
    d = demand_by_reference(CTX["pme_ref"])
    for _ in range(5):
        step = current_step(d["id"])
        if step["stepCode"] == "INSURANCE_VALIDATION_STEP":
            break
        advance(d["id"], step["assignedUsername"], next_step_staff(d["id"], step["stepCode"]))
    step = current_step(d["id"])
    check(step["stepCode"] == "INSURANCE_VALIDATION_STEP", f"stuck at {step['stepCode']}")
    nxt = next_step_staff(d["id"], step["stepCode"])
    advance(d["id"], step["assignedUsername"], nxt, expect=422)
    advance(d["id"], step["assignedUsername"], nxt, insuranceAdiSubscribed=True,
            insuranceCoverageType="BANK_GROUP_POLICY", insuranceDamageSubscribed=True)

@scenario("E-09", "Last step passed -> dossier COMPLETED, credit file opened with the full schedule at the agreed rate")
def e09():
    d = demand_by_reference(CTX["pme_ref"])
    for _ in range(5):
        dd = demand_by_reference(CTX["pme_ref"])
        if dd["completed"]:
            break
        step = current_step(d["id"])
        advance(d["id"], step["assignedUsername"], next_step_staff(d["id"], step["stepCode"]))
    check(demand_by_reference(CTX["pme_ref"])["completed"], "dossier not completed")
    cf = ADMIN.get(f"/api/v1/demands/{d['id']}/credit-file").json()
    CTX["credit_file"] = cf
    check(len(cf["installments"]) == 84, f"expected 84 installments, got {len(cf['installments'])}")
    check(cf["beneficiaryRib"], "beneficiary RIB missing")
    ADMIN.post(f"/api/v1/demands/{d['id']}/advance", KEEP_ALL, expect=(403, 409))

def installments(client, demand_id):
    return client.get(f"/api/v1/me/demands/{demand_id}/credit-file").json()["installments"]

@scenario("H-01", "Client sees the credit file; declares a payment with a bad RIB -> 400; valid -> DECLARED; "
                  "twice -> 409")
def h01():
    d = demand_by_reference(CTX["pme_ref"])
    client = SESSIONS["invited"]
    first = installments(client, d["id"])[0]
    base = f"/api/v1/me/demands/{d['id']}/credit-file/installments/{first['id']}/payments"
    r = client.post(base, {"sourceRib": RIB_BAD_KEY}, expect=None)
    check(r.status_code == 400, f"bad RIB returned {r.status_code}")
    CTX["bad_rib_message"] = r.json().get("description")
    declared = client.post(base, {"sourceRib": RIB_OTHER}).json()
    check(declared["status"] == "DECLARED", f"status {declared['status']}")
    client.post(base, {"sourceRib": RIB_OTHER}, expect=409)
    CTX["inst1"] = first["id"]

@scenario("H-02", "Manager rejects the declaration (not yet due -> PENDING), client declares again, manager "
                  "validates -> PAID")
def h02():
    d = demand_by_reference(CTX["pme_ref"])
    base = f"/api/v1/demands/{d['id']}/credit-file/installments/{CTX['inst1']}/payments"
    rejected = ADMIN.post(base + "/reject", {"reason": "Virement introuvable"}).json()
    check(rejected["status"] == "PENDING", f"expected PENDING after rejection, got {rejected['status']}")
    SESSIONS["invited"].post(f"/api/v1/me/demands/{d['id']}/credit-file/installments/{CTX['inst1']}/payments",
                             {"sourceRib": RIB_OTHER})
    paid = ADMIN.post(base + "/validate").json()
    check(paid["status"] == "PAID", f"expected PAID, got {paid['status']}")
    ADMIN.post(base + "/validate", expect=409)

@scenario("H-03", "Validate an installment the client never declared -> 409")
def h03():
    d = demand_by_reference(CTX["pme_ref"])
    second = installments(SESSIONS["invited"], d["id"])[1]
    ADMIN.post(f"/api/v1/demands/{d['id']}/credit-file/installments/{second['id']}/payments/validate", expect=409)

@scenario("H-04", "Client is notified by email of the payment received / rejected (bell is back-office only)")
def h04():
    wait_for_mail(f"contact.{RUN}@atlas.ma", r"(?i)DEM-", timeout=15)
    client = SESSIONS["invited"]
    client.get("/api/v1/notifications?page=1&size=5", expect=403)
    staff = as_user(staff_for("CHARGE_AFFAIRES", "RESEAU", 1))
    staff.get("/api/v1/notifications?page=1&size=5")
    staff.patch("/api/v1/notifications/read-all", expect=(200, 204))

@scenario("I-01", "Early repayment quote (client) then PARTIAL by the manager -> lower installments")
def i01():
    d = demand_by_reference(CTX["pme_ref"])
    q = SESSIONS["invited"].get(f"/api/v1/me/demands/{d['id']}/credit-file/early-repayments/quote").json()
    CTX["outstanding"] = float(q["outstandingBefore"])
    check(q["fullRepayment"] and q["indemnity"] <= 0.03 * CTX["outstanding"] + 0.01, f"indemnity above the loi 31-08 cap: {q}")
    check(CTX["outstanding"] > 0, f"quote without outstanding capital: {q}")
    before = installments(SESSIONS["invited"], d["id"])[-1]["amount"]
    ADMIN.get(f"/api/v1/demands/{d['id']}/credit-file/early-repayments/quote?amount={CTX['outstanding'] + 1}",
              expect=422)
    ADMIN.post(f"/api/v1/demands/{d['id']}/credit-file/early-repayments", {"amount": 200000}, expect=(200, 201))
    after = installments(SESSIONS["invited"], d["id"])[-1]["amount"]
    check(float(after) < float(before), f"installment not reduced ({before} -> {after})")

@scenario("I-02", "TOTAL early repayment -> remaining installments REPAID_EARLY, credit file CLOSED, "
                  "declaring afterwards -> 409")
def i02():
    d = demand_by_reference(CTX["pme_ref"])
    ADMIN.post(f"/api/v1/demands/{d['id']}/credit-file/early-repayments", {}, expect=(200, 201))
    cf = ADMIN.get(f"/api/v1/demands/{d['id']}/credit-file").json()
    check(cf["status"] == "CLOSED", f"credit file status {cf['status']}")
    last = cf["installments"][-1]
    check(last["status"] == "REPAID_EARLY", f"last installment {last['status']}")
    SESSIONS["invited"].post(f"/api/v1/me/demands/{d['id']}/credit-file/installments/{last['id']}/payments",
                             {"sourceRib": RIB_OTHER}, expect=409)
    check(len(ADMIN.get(f"/api/v1/demands/{d['id']}/credit-file/early-repayments").json()) == 2, "history size")

@scenario("J-01", "Mortgage: classify REAL (no enterprise needed) then four-eyes blocks the instructor from deciding")
def j01():
    d = demand_by_reference(CTX["mortgage_ref"])
    ca = as_user(staff_for("CHARGE_AFFAIRES", None) if ("CHARGE_AFFAIRES", None) in STAFF
                 else staff_for("CHARGE_AFFAIRES", "RESEAU"))
    instructor = staff_for(None, None, 0)
    ca.post(f"/api/v1/demands/{d['id']}/classify", {"classificationOutcome": "REAL", "assigneeUsername": instructor})
    advance(d["id"], instructor, instructor)
    check(current_step(d["id"])["stepCode"] == "MORTGAGE_DECISION_STEP", "not at decision")
    r = advance(d["id"], instructor, instructor, expect=None, decisionOutcome="APPROVED")
    check(r.status_code == 403, f"four-eyes not enforced ({r.status_code}): {r.text[:200]}")
    as_user(instructor).post(f"/api/v1/demands/{d['id']}/assign", {"username": staff_for(None, None, 1)})

@scenario("J-02", "Mortgage: second pair of eyes decides, dossier goes to disbursement, credit file opens")
def j02():
    d = demand_by_reference(CTX["mortgage_ref"])
    decider = staff_for(None, None, 1)
    advance(d["id"], decider, decider, decisionOutcome="APPROVED")
    for _ in range(5):
        if demand_by_reference(CTX["mortgage_ref"])["completed"]:
            break
        step = current_step(d["id"])
        advance(d["id"], step["assignedUsername"], step["assignedUsername"])
    check(demand_by_reference(CTX["mortgage_ref"])["completed"], "mortgage dossier not completed")
    cf = ADMIN.get(f"/api/v1/demands/{d['id']}/credit-file").json()
    check(len(cf["installments"]) == 240, f"{len(cf['installments'])} installments")
    CTX["mortgage_cf"] = cf

@scenario("J-03", "Mortgage borrower (no enterprise): invited by e-mail at classification, creates an account, "
                  "sees only their dossier")
def j03():
    d = demand_by_reference(CTX["mortgage_ref"])
    email = f"karim.{RUN}@example.ma"
    token = wait_for_mail(email, r"invitations/([A-Za-z0-9_\-]+)").group(1)
    preview = PUBLIC.get(f"/api/v1/invitations/{token}").json()
    check(preview["demandReference"] == CTX["mortgage_ref"] and preview["enterpriseName"] is None,
          f"preview should name the dossier, not an enterprise: {preview}")
    PUBLIC.post(f"/api/v1/invitations/{token}/accept", {"username": f"karim.{RUN}", "password": CLIENT_PASSWORD},
                expect=(200, 201, 204))
    borrower = Api("mortgage-borrower")
    borrower.login(f"karim.{RUN}", CLIENT_PASSWORD)
    refs = [x["reference"] for x in borrower.get("/api/v1/me/mortgage-demands").json()]
    check(refs == [CTX["mortgage_ref"]], f"borrower should see exactly their dossier, got {refs}")
    check(borrower.get("/api/v1/me/portfolio").json() == [], "a mortgage borrower has no enterprise portfolio")
    SESSIONS["borrower"] = borrower

    stranger = SESSIONS["stranger"]
    stranger.get(f"/api/v1/demands/{d['id']}", expect=403)
    stranger.get(f"/api/v1/me/demands/{d['id']}/credit-file", expect=403)
    check(stranger.get("/api/v1/me/mortgage-demands").json() == [], "a stranger must not see anyone's dossier")

@scenario("J-06", "Mortgage borrower declares an installment payment, the manager validates it -> PAID")
def j06():
    d = demand_by_reference(CTX["mortgage_ref"])
    borrower = SESSIONS["borrower"]
    cf = borrower.get(f"/api/v1/me/demands/{d['id']}/credit-file").json()
    first = cf["installments"][0]["id"]
    base = f"/api/v1/me/demands/{d['id']}/credit-file/installments/{first}/payments"
    declared = borrower.post(base, {"sourceRib": RIB_OTHER}).json()
    check(declared["status"] == "DECLARED", f"status {declared['status']}")
    paid = ADMIN.post(f"/api/v1/demands/{d['id']}/credit-file/installments/{first}/payments/validate").json()
    check(paid["status"] == "PAID", f"expected PAID, got {paid['status']}")

@scenario("J-04", "Mortgage committee REJECTED -> dossier REJECTED, client notified by email, no credit file")
def j04():
    ref = PUBLIC.post("/api/v1/mortgage-credit", mortgage_body(email=f"rejected.{RUN}@x.ma",
                                                                applicantName=f"Rejected {RUN}")).json()["demandReference"]
    d = demand_by_reference(ref)
    ca = as_user(staff_for("CHARGE_AFFAIRES", "RESEAU"))
    i1, i2 = staff_for(None, None, 0), staff_for(None, None, 1)
    ca.post(f"/api/v1/demands/{d['id']}/classify", {"classificationOutcome": "REAL", "assigneeUsername": i1})
    advance(d["id"], i1, i2)
    advance(d["id"], i2, i2, decisionOutcome="REJECTED")
    check(demand_by_reference(ref)["rejected"], "not rejected")
    ADMIN.get(f"/api/v1/demands/{d['id']}/credit-file", expect=404)
    wait_for_mail(f"rejected.{RUN}@x.ma", r"(?i)DEM-")

@scenario("J-05", "Mourabaha: early repayment is refused (422) once its credit file exists")
def j05():
    d = demand_by_reference(CTX["mourabaha_ref"])
    ca = as_user(staff_for("CHARGE_AFFAIRES", "RESEAU"))
    i1, i2 = staff_for(None, None, 0), staff_for(None, None, 1)
    ca.post(f"/api/v1/demands/{d['id']}/classify", {"classificationOutcome": "REAL", "assigneeUsername": i1})
    advance(d["id"], i1, i2)
    advance(d["id"], i2, i2, decisionOutcome="APPROVED")
    for _ in range(5):
        if demand_by_reference(CTX["mourabaha_ref"])["completed"]:
            break
        step = current_step(d["id"])
        advance(d["id"], step["assignedUsername"], step["assignedUsername"])
    ADMIN.get(f"/api/v1/demands/{d['id']}/credit-file/early-repayments/quote", expect=422)

@scenario("K-01", "Credit-rate grid with rateMax above the TMIC (14%) -> 422, nothing saved")
def k01():
    rates = ADMIN.get("/api/v1/credit-rates").json()
    sala = [r for r in (rates if isinstance(rates, list) else rates["items"]) if r["cspCode"] == "SALA"][0]
    ADMIN.patch(f"/api/v1/credit-rates/{sala['id']}", {"rateMax": 15}, expect=422)

@scenario("K-02", "BAM parameters update -> new active version, audited field by field")
def k02():
    def active():
        data = ADMIN.get("/api/v1/bam-parameters").json()
        items = data if isinstance(data, list) else data.get("items", [data])
        return [b for b in items if b.get("active", True)][0]
    ADMIN.patch(f"/api/v1/bam-parameters/{active()['id']}", {"maxAgeAtMaturity": 71}, expect=(200, 204))
    audit = ADMIN.get("/api/v1/audit-log?page=1&size=5").json()["items"]
    check(any(a["action"] == "BAM_PARAMETERS_UPDATE" for a in audit), "BAM update not audited")
    ADMIN.patch(f"/api/v1/bam-parameters/{active()['id']}", {"maxAgeAtMaturity": 70}, expect=(200, 204))

@scenario("K-03", "Document suggestions: create (CSP scope), duplicate -> 409, delete")
def k03():
    s = ADMIN.post("/api/v1/document-suggestions", {"cspCode": "SALA", "label": f"Attestation {RUN}"},
                   expect=(200, 201)).json()
    ADMIN.post("/api/v1/document-suggestions", {"cspCode": "SALA", "label": f"Attestation {RUN}"}, expect=409)
    ADMIN.post("/api/v1/document-suggestions", {"cspCode": "SALA", "enterpriseType": "PME", "label": "x"},
               expect=(400, 409, 422))
    ADMIN.delete(f"/api/v1/document-suggestions/{s['id']}")

@scenario("K-04", "BAM verification mock answers for an enterprise identifier")
def k04():
    ADMIN.post("/api/v1/mock/verification-bam", {"enterpriseType": "PME", "identifier": f"RC-{RUN}",
                                                  "companyName": "Atlas"})

def main():
    journey("Setup")
    setup_staff()
    journey("A. Mortgage simulation")
    for f in (a01, a02, a03, a04, a05, a06, a07, a08, a09):
        f()
    journey("B. Investment simulation")
    for f in (b01, b02, b03, b04, b05, b06, b07):
        f()
    journey("C. Accounts & security")
    for f in (c01, c02, c03):
        f()
    journey("D. Classification")
    for f in (d01, d02, d03, d04, d05, d06):
        f()
    journey("G. Client access")
    for f in (g01, g02, g03, g04):
        f()
    journey("F. Documents")
    for f in (f01, f02, f03, f04, f05):
        f()
    journey("E. Instruction pipeline (PME)")
    for f in (e01, e02, e03, e04, e05, e06, e07, e08, e09):
        f()
    journey("H. Payments")
    for f in (h01, h02, h03, h04):
        f()
    journey("I. Early repayment")
    for f in (i01, i02):
        f()
    journey("J. Mortgage pipeline")
    for f in (j01, j02, j03, j06, j04, j05):
        f()
    journey("K. Parameters & reference data")
    for f in (k01, k02, k03, k04):
        f()

if __name__ == "__main__":
    main_guard(main)
