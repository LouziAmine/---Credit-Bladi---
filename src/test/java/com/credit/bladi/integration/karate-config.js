function fn() {

    let baseUrl      = karate.properties["karate.baseUrl"];
    let managerToken = karate.properties["karate.managerToken"];
    let adminUsername = karate.properties["karate.adminUsername"];
    let adminPassword = karate.properties["karate.adminPassword"];
    let requestId    = 'karate_' + java.util.UUID.randomUUID();

    let authHeaders = {
        'Authorization': managerToken,
        'Content-Type': 'application/json',
        'x-request-id': requestId
    };

    let publicHeaders = {
        'Content-Type': 'application/json',
        'x-request-id': requestId
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

    karate.log('Karate configuration loaded — baseUrl:', baseUrl);

    return config;
}