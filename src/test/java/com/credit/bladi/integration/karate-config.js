function fn() {

    let baseUrl      = karate.properties["karate.baseUrl"];
    let managerToken = karate.properties["karate.managerToken"];
    let adminUsername = karate.properties["karate.adminUsername"];
    let adminPassword = karate.properties["karate.adminPassword"];
    let requestId    = 'karate_' + java.util.UUID.randomUUID();

    let authHeaders = {
        'Authorization': managerToken,
        'Content-Type': 'application/json',
        'x-request-id': requestId,
        'Accept-Language': 'en'
    };

    let publicHeaders = {
        'Content-Type': 'application/json',
        'x-request-id': requestId,
        'Accept-Language': 'en'
    };

    var config = {
        baseUrl: baseUrl,
        managerToken: managerToken,
        adminUsername: adminUsername,
        adminPassword: adminPassword,
        authHeaders: authHeaders,
        publicHeaders: publicHeaders
    };

    karate.configure('connectTimeout', 20000);
    karate.configure('readTimeout', 20000);
    karate.configure('printEnabled', true);
    karate.configure('logPrettyRequest', true);
    karate.configure('logPrettyResponse', true);
    // Applied as defaults to every request in every feature, even scenarios that set headers
    // ad hoc (e.g. `header Authorization = someToken`) instead of spreading authHeaders/publicHeaders, 
    // keeps error message assertions in English regardless of how a scenario builds its headers.
    karate.configure('headers', { 'Accept-Language': 'en' });

    karate.log('Karate configuration loaded: baseUrl:', baseUrl);

    return config;
}