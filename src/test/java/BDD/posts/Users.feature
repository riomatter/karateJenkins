Feature: Posts
  Scenario: Verificar usuario
    * def expected = read('classpath:BDD/posts/estructuraJsonEsperada.json')
    Given url 'https://jsonplaceholder.typicode.com'
    And path '/posts'
    And param userId = 1
    When method GET
    Then status 200
    And match responseType == 'json'
    And match response[0] == expected
    And match response[0].userId == 1
    And assert response.length > 1
    * print "Response:" , response[0].userId


#Feature: Prueba real con Karate
#
#  Background:
#    * def expected = read('classpath:BDD/posts/formfields.json')
#    Given url URLauth
#    And path PATHauth
#    And headers {Content-Type: 'application/x-www-form-urlencoded' , Cookie: 'AWSALBAPP-0=_remove_; AWSALBAPP-1=_remove_; AWSALBAPP-2=_remove_; AWSALBAPP-3=_remove_'}
#    And form fields { "client_id": "#(client_id)", "username": "#(username)","password": "#(password)","grant_type": "#(grant_type)","scope": "#(scope)"}
#    Then status 200
#    And match response.access_token != null
#    And match response.access_token != ''
#    * eval karate.set('authToken', 'Bearer ' + response.access_token)
#   And print authToken
#
#
#  Scenario Outline: Status code 200 Obtener recursos de la guia
#    * def expected = read('classpath:BDD/posts/estructuraEsperada200.json')
#    Given url URLrecursos
#    And path PATHrecursos + "<ID>" + '/recursos'
#    And headers {user-id: "<user-id>" , user-role: "<user-role>", authorization: '#(authToken)' }
#    When method GET
#    Then status 200
#    And match each response.data == expected.data[0]
#
#    Examples:
#      | ID  | user-id      | user-role     |
#      | 1   | usrsilbia10  | Editor_Guias  |
#      | 2   | usrsilbia10  | Editor_Guias  |
#
#  Scenario: Registro exitoso en reqres
#    * def expected = read('classpath:BDD/posts/headers.json')
#    * def requestJson = read('classpath:BDD/posts/request.json')
#    Given url 'https://reqres.in/api/register'
#    And headers expected
#    And request requestJson
#    When method POST
#    Then status 200