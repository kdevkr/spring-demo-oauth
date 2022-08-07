## ES256 - ECDSA using P-256 and SHA-256

_Generate EC key-pair and extract pkcs8_
```shell
openssl ecparam -name prime256v1 -genkey -noout -out ec-private.pem
openssl ec -in ec-private.pem -pubout -out ec-public.pem
openssl pkcs8 -topk8 -inform pem -in ec-private.pem -outform pem -nocrypt -out ec-private.pkcs8
```
