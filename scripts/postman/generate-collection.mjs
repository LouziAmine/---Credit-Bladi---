#!/usr/bin/env node
// Regenerates the Postman collection and environment from the running API's OpenAPI document.
//
//   node scripts/postman/generate-collection.mjs [openapi-url]
//
// Default URL: http://localhost:8080/swagger/credit-bladi/v3/api-docs.json (dev profile).
// Output: postman/Credit-Bladi.postman_collection.json and postman/Credit-Bladi.postman_environment.json.
//
// The collection has two parts:
//   1. End-to-end journey: an ordered, self-contained scenario with tests, run as is by Newman
//      (`npx newman run postman/Credit-Bladi.postman_collection.json -e postman/Credit-Bladi.postman_environment.json --folder "1. End-to-end journey"`).
//   2. API reference: every operation of the OpenAPI document, grouped by tag, with an example body.
import { mkdirSync, writeFileSync } from 'node:fs';
import { dirname, join } from 'node:path';
import { fileURLToPath } from 'node:url';

const ROOT = join(dirname(fileURLToPath(import.meta.url)), '..', '..');
const OPENAPI_URL = process.argv[2] ?? 'http://localhost:8080/swagger/credit-bladi/v3/api-docs.json';
const VERSION = '2.1.0';

const api = await (await fetch(OPENAPI_URL)).json();
const schemas = api.components?.schemas ?? {};

// ---------- 1. End-to-end journey ----------

const json = (body) => ({ mode: 'raw', raw: JSON.stringify(body, null, 2), options: { raw: { language: 'json' } } });
const status = (code) => `pm.test('status ${code}', () => pm.response.to.have.status(${code}));`;

function req(name, method, path, { body, tests = [], pre = [], description } = {}) {
  const [pathPart, query] = path.split('?');
  const item = {
    name,
    request: {
      method,
      header: body ? [{ key: 'Content-Type', value: 'application/json' }] : [],
      url: {
        raw: `{{baseUrl}}${path}`,
        host: ['{{baseUrl}}'],
        path: pathPart.split('/').filter(Boolean),
        ...(query ? { query: query.split('&').map((kv) => { const [key, value] = kv.split('='); return { key, value }; }) } : {}),
      },
      ...(body ? { body: json(body) } : {}),
      ...(description ? { description } : {}),
    },
    event: [],
  };
  if (pre.length) item.event.push({ listen: 'prerequest', script: { type: 'text/javascript', exec: pre } });
  if (tests.length) item.event.push({ listen: 'test', script: { type: 'text/javascript', exec: tests } });
  return item;
}

const KEEP = { rateOption: 'KEEP', amountOption: 'KEEP', durationOption: 'KEEP' };
const findDemand = (refVar, idVar) => [
  status(200),
  `const ref = pm.collectionVariables.get('${refVar}');`,
  'const found = pm.response.json().items.find((d) => d.reference === ref);',
  `pm.test('dossier ' + ref + ' is listed', () => pm.expect(found, 'not on the first page').to.be.an('object'));`,
  `pm.collectionVariables.set('${idVar}', found.id);`,
];

const journey = {
  name: '1. End-to-end journey',
  description: 'A complete, ordered scenario on a fresh dev database: staff accounts, simulations, classification, '
    + 'four-eyes rule, decision, disbursement, credit file, client portal and access rules. Every request has tests. '
    + 'A unique suffix per run keeps it repeatable. It logs in four times, under the default limit of five logins per minute.',
  item: [
    req('01 Log in as the administrator', 'POST', '/api/v1/auth/login', {
      body: { username: '{{adminUsername}}', password: '{{adminPassword}}' },
      pre: ["pm.collectionVariables.set('run', Date.now().toString(36));"],
      tests: [status(200), "pm.test('session cookie set', () => pm.expect(pm.cookies.has('cb_access_token')).to.be.true);"],
    }),
    req('02 Who am I', 'GET', '/api/v1/auth/me', { tests: [status(200), "pm.test('manager', () => pm.expect(pm.response.json().role).to.eql('MANAGER'));"] }),
    req('03 Grades', 'GET', '/api/v1/grades', { tests: [status(200)] }),
    req('04 Teams', 'GET', '/api/v1/teams', { tests: [status(200)] }),
    req('05 Create the account officer (CHARGE_AFFAIRES, RESEAU)', 'POST', '/api/v1/users', {
      body: { username: 'officer.{{run}}', fullName: 'Sara Bennani', email: 'officer.{{run}}@bladi-credit.ma', password: '{{staffPassword}}', role: 'MANAGER', gradeCode: 'CHARGE_AFFAIRES', teamCode: 'RESEAU' },
      tests: [status(201)],
    }),
    req('06 Create the branch director (decides mortgages)', 'POST', '/api/v1/users', {
      body: { username: 'director.{{run}}', fullName: 'Karim Tazi', email: 'director.{{run}}@bladi-credit.ma', password: '{{staffPassword}}', role: 'MANAGER' },
      tests: [status(201)],
    }),
    req('07 Create the client company', 'POST', '/api/v1/enterprises', {
      body: { name: 'Argane Cosmetiques Agadir {{run}}', sector: 'Industrie', registrationNumber: 'RC-AGADIR-{{run}}' },
      tests: [status(201), 'const e = pm.response.json();', "pm.collectionVariables.set('enterpriseId', e.id ?? e.enterpriseId);"],
    }),
    req('08 Mortgage simulation refused (debt ratio above 40 %)', 'POST', '/api/v1/mortgage-credit', {
      body: { applicantName: 'Mehdi Lahlou', email: 'mehdi.{{run}}@example.ma', phone: '0661457823', amount: 950000, duration: 240, cspCode: 'SALA', productType: 'CONVENTIONAL', nationalityCode: 'MA', moroccanResident: true, propertyValue: 1200000, monthlyIncome: 9000, age: 34 },
      tests: [status(422), "pm.test('the reason is explained', () => pm.expect(pm.response.json().description).to.include('40'));"],
    }),
    req('09 Mortgage simulation accepted with a co-borrower', 'POST', '/api/v1/mortgage-credit', {
      body: { applicantName: 'Mehdi Lahlou', email: 'mehdi.{{run}}@example.ma', phone: '0661457823', amount: 950000, duration: 240, cspCode: 'SALA', productType: 'CONVENTIONAL', nationalityCode: 'MA', moroccanResident: true, propertyValue: 1200000, monthlyIncome: 9000, coBorrower: true, coBorrowerMonthlyIncome: 10000, age: 34 },
      tests: [status(200), "pm.collectionVariables.set('mortgageRef', pm.response.json().demandReference);", "pm.test('a dossier is opened', () => pm.expect(pm.response.json().demandReference).to.match(/^DEM-/));"],
    }),
    req('10 Investment simulation (PME) accepted', 'POST', '/api/v1/investment-credit', {
      body: { companyName: 'Argane Cosmetiques Agadir {{run}}', enterpriseType: 'PME', sector: 'Industrie', email: 'contact.{{run}}@argane.ma', phone: '0528214567', amount: 2500000, duration: 84, annualRevenue: 14000000, companyAgeYears: 7, existingAnnualDebtService: 150000, netAnnualResult: 1900000, guaranteesValue: 2200000 },
      tests: [status(200), "pm.collectionVariables.set('investmentRef', pm.response.json().demandReference);"],
    }),
    req('11 Log in as the account officer', 'POST', '/api/v1/auth/login', { body: { username: 'officer.{{run}}', password: '{{staffPassword}}' }, tests: [status(200)] }),
    req('12 Find the mortgage dossier', 'GET', '/api/v1/demands?page=1&size=50', { tests: findDemand('mortgageRef', 'mortgageId') }),
    req('13 Its six-step pipeline', 'GET', '/api/v1/demands/{{mortgageId}}/pipeline', { tests: [status(200), "pm.test('six steps', () => pm.expect(pm.response.json()).to.have.length(6));"] }),
    req('14 Classify it as real, keep the instruction', 'POST', '/api/v1/demands/{{mortgageId}}/classify', {
      body: { classificationOutcome: 'REAL', assigneeUsername: 'officer.{{run}}', note: 'Client reached by phone, project confirmed.' },
      tests: [status(200)],
    }),
    req('15 Add a remark', 'POST', '/api/v1/demands/{{mortgageId}}/comments', { body: { comment: 'Payslips requested from the client.' }, tests: [status(201)] }),
    req('16 Ask the client for a document', 'POST', '/api/v1/demands/{{mortgageId}}/document-requests', { body: { documentType: 'Payslips (last 3 months)' }, tests: [status(200)] }),
    req('17 Close the instruction, keep the decision step', 'POST', '/api/v1/demands/{{mortgageId}}/advance', {
      body: { ...KEEP, assigneeUsername: 'officer.{{run}}', note: 'Household debt ratio 37 %.' }, tests: [status(200)],
    }),
    req('18 Four-eyes rule: the instructor cannot decide', 'POST', '/api/v1/demands/{{mortgageId}}/advance', {
      body: { ...KEEP, decisionOutcome: 'APPROVED', assigneeUsername: 'director.{{run}}' }, tests: [status(403)],
    }),
    req('19 Hand the decision to the director', 'POST', '/api/v1/demands/{{mortgageId}}/assign', { body: { username: 'director.{{run}}' }, tests: [status(200)] }),
    req('20 Find the investment dossier', 'GET', '/api/v1/demands?page=1&size=50', { tests: findDemand('investmentRef', 'investmentId') }),
    req('21 Classify it as real and attach the company', 'POST', '/api/v1/demands/{{investmentId}}/classify', {
      body: { classificationOutcome: 'REAL', enterpriseId: '{{enterpriseId}}', clientRib: '230 780 0123456789012345 03', assigneeUsername: 'officer.{{run}}', note: 'Meeting with the manager in Agadir.' },
      tests: [status(200)],
    }),
    req('22 Log in as the director', 'POST', '/api/v1/auth/login', { body: { username: 'director.{{run}}', password: '{{staffPassword}}' }, tests: [status(200)] }),
    req('23 Grant the credit', 'POST', '/api/v1/demands/{{mortgageId}}/advance', {
      body: { ...KEEP, decisionOutcome: 'APPROVED', assigneeUsername: 'director.{{run}}', note: 'Granted, 21 % down payment.' }, tests: [status(200)],
    }),
    req('24 Edit the loan offer', 'POST', '/api/v1/demands/{{mortgageId}}/advance', { body: { ...KEEP, assigneeUsername: 'director.{{run}}' }, tests: [status(200)] }),
    req('25 Register the mortgage guarantee', 'POST', '/api/v1/demands/{{mortgageId}}/advance', { body: { ...KEEP, assigneeUsername: 'director.{{run}}' }, tests: [status(200)] }),
    req('26 Disburse the funds', 'POST', '/api/v1/demands/{{mortgageId}}/advance', { body: { ...KEEP, note: 'Funds released to the seller.' }, tests: [status(200)] }),
    req('27 The credit file and its schedule', 'GET', '/api/v1/demands/{{mortgageId}}/credit-file', {
      tests: [status(200), "pm.test('240 installments', () => pm.expect(pm.response.json().installments).to.have.length(240));"],
    }),
    req('28 Early repayment quote', 'GET', '/api/v1/demands/{{mortgageId}}/credit-file/early-repayments/quote?amount=100000', { tests: [status(200)] }),
    req('29 Audit log', 'GET', '/api/v1/audit-log', { tests: [status(200)] }),
    req('30 Register a client account', 'POST', '/api/v1/auth/register', { body: { username: 'client.{{run}}', password: '{{clientPassword}}' }, tests: [status(201)] }),
    req('31 Log in as the client', 'POST', '/api/v1/auth/login', { body: { username: 'client.{{run}}', password: '{{clientPassword}}' }, tests: [status(200)] }),
    req('32 A client cannot reach the back office', 'GET', '/api/v1/demands', { tests: [status(403)] }),
    req('33 The client portfolio', 'GET', '/api/v1/me/portfolio', { tests: [status(200)] }),
    req('34 Log out', 'POST', '/api/v1/auth/logout', { tests: ["pm.test('logged out', () => pm.expect(pm.response.code).to.be.oneOf([200, 204]));"] }),
    req('35 No session, no access', 'GET', '/api/v1/auth/me', { tests: [status(401)] }),
  ],
};

// ---------- 2. API reference, generated from the OpenAPI document ----------

const resolve = (s) => (s?.$ref ? schemas[s.$ref.split('/').pop()] : s);
function example(schema, depth = 0) {
  const s = resolve(schema);
  if (!s || depth > 4) return null;
  if (s.example !== undefined) return s.example;
  if (s.type === 'array') return [example(s.items, depth + 1)];
  if (s.type === 'object' || s.properties) {
    return Object.fromEntries(Object.entries(s.properties ?? {}).map(([k, v]) => [k, example(v, depth + 1)]));
  }
  if (s.enum) return s.enum[0];
  return { string: '', integer: 0, number: 0, boolean: false }[s.type] ?? null;
}

const METHODS = ['get', 'post', 'put', 'patch', 'delete'];
const byTag = new Map();
for (const [path, ops] of Object.entries(api.paths)) {
  for (const method of METHODS) {
    const op = ops[method];
    if (!op) continue;
    const tag = op.tags?.[0] ?? 'Other';
    const params = op.parameters ?? [];
    const query = params.filter((p) => p.in === 'query').map((p) => ({
      key: p.name, value: p.example !== undefined ? String(p.example) : '', disabled: !p.required, description: p.description,
    }));
    const url = `{{baseUrl}}${path.replace(/\{(\w+)\}/g, '{{$1}}')}`;
    const request = {
      method: method.toUpperCase(),
      header: [],
      url: { raw: url, host: ['{{baseUrl}}'], path: path.replace(/\{(\w+)\}/g, '{{$1}}').split('/').filter(Boolean), ...(query.length ? { query } : {}) },
      description: [op.summary, op.description].filter(Boolean).join('\n\n'),
    };
    const content = op.requestBody?.content ?? {};
    const type = Object.keys(content)[0];
    const bodySchema = resolve(content[type]?.schema);
    const fileFields = Object.entries(bodySchema?.properties ?? {}).filter(([, v]) => v.format === 'binary');
    if (fileFields.length) {
      request.body = { mode: 'formdata', formdata: fileFields.map(([key]) => ({ key, type: 'file', src: [] })) };
    } else if (type) {
      request.header.push({ key: 'Content-Type', value: 'application/json' });
      request.body = json(example(content[type].schema));
    }
    if (!byTag.has(tag)) byTag.set(tag, []);
    byTag.get(tag).push({ name: op.summary ?? op.operationId, request });
  }
}

const reference = {
  name: '2. API reference',
  description: `Every operation of the API ${VERSION}, generated from its OpenAPI document and grouped like Swagger. `
    + 'Log in first (`1. End-to-end journey / 01`, or the Authentication folder): the session cookies are reused automatically. '
    + 'Fill the path variables ({{id}}, {{demandId}}...) with ids taken from the responses.',
  item: [...byTag.entries()].sort(([a], [b]) => a.localeCompare(b)).map(([tag, item]) => ({ name: tag, item })),
};

// ---------- Collection and environment ----------

const collection = {
  info: {
    _postman_id: 'cb-credit-bladi-api',
    name: `Credit Bladi API ${VERSION}`,
    description: `Postman collection of the Credit Bladi API ${VERSION}, generated by \`scripts/postman/generate-collection.mjs\` `
      + 'from the running OpenAPI document: regenerate it after any API change.\n\n'
      + '**Sessions**: login sets two HttpOnly cookies (`cb_access_token`, `cb_refresh_token`); Postman and Newman keep them in '
      + 'their cookie jar, so every following request runs as the last user who logged in.\n\n'
      + '**Run it** against the `dev` profile (`docker compose --profile app up -d`): '
      + '`npx newman run postman/Credit-Bladi.postman_collection.json -e postman/Credit-Bladi.postman_environment.json --folder "1. End-to-end journey"`.',
    schema: 'https://schema.getpostman.com/json/collection/v2.1.0/collection.json',
  },
  item: [journey, reference],
  variable: ['run', 'mortgageRef', 'mortgageId', 'investmentRef', 'investmentId', 'enterpriseId'].map((key) => ({ key, value: '' })),
};

const environment = {
  id: 'cb-env-local',
  name: 'Credit Bladi: local (dev profile)',
  values: [
    { key: 'baseUrl', value: 'http://localhost:8080', type: 'default', enabled: true },
    { key: 'adminUsername', value: 'admin', type: 'default', enabled: true, description: 'Demo manager seeded by the dev profile only' },
    { key: 'adminPassword', value: 'admin123', type: 'secret', enabled: true, description: 'Dev profile only, never deployed' },
    { key: 'staffPassword', value: 'Staff!Bladi2026', type: 'secret', enabled: true, description: 'Password given to the staff accounts the journey creates' },
    { key: 'clientPassword', value: 'Client!Bladi2026', type: 'secret', enabled: true, description: 'Password of the client account the journey registers' },
  ],
  _postman_variable_scope: 'environment',
};

mkdirSync(join(ROOT, 'postman'), { recursive: true });
writeFileSync(join(ROOT, 'postman', 'Credit-Bladi.postman_collection.json'), `${JSON.stringify(collection, null, 2)}\n`);
writeFileSync(join(ROOT, 'postman', 'Credit-Bladi.postman_environment.json'), `${JSON.stringify(environment, null, 2)}\n`);
const operations = reference.item.reduce((n, folder) => n + folder.item.length, 0);
console.log(`journey: ${journey.item.length} requests; reference: ${operations} operations in ${reference.item.length} folders`);
