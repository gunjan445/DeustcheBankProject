# DeustcheBankProject
challange code test
#create contact using the url
http://localhost:18080/v1/accounts
#type:post
#Add headers
Content-Type:application/json
#Add body
{
"accountId":1,
"balance":10000
}
{
"accountId":2,
"balance":10000
}
#check balance of respective account by url
http://localhost:18080/v1/accounts/1
http://localhost:18080/v1/accounts/2
#type:GET

#Transfer funds using below url
http://localhost:18080/v1/transfer
#type:post
#Headers
content-Type:application/json
#body
{
"fromAccountId":"1",
"toAccountId":"2",
"balance":500
}
#After that check respective accounts by
http://localhost:18080/v1/accounts/1
http://localhost:18080/v1/accounts/2
