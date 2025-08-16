function fn() {

  var configUTP ={
    URLauth: 'https://sso-dev.utp.edu.pe',
    PATHauth: '/auth/realms/Xpedition/protocol/openid-connect/token',
    URLrecursos: 'https://api-silbia2-dev.utpxpedition.com',
    PATHrecursos: '/silbia/guias/api/v1/guia-laboratorio/',
    client_id: 'silbia2-web',
    username: 'usrsilbia10',
    password: 'utp2024',
    grant_type: 'password',
    scope: 'openid'
  };
  karate.log('Loaded karate-config.js successfully');
  return configUTP;
}