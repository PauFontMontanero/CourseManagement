# Keystore Generation Commands

1. Generate server keystore:
   keytool -genkey -keyalg RSA -alias server -keystore server.p12 -storepass Teknos01. -validity 360 -keysize 2048

2. Generate client keystore:
   keytool -genkey -keyalg RSA -alias client1 -keystore client1.p12 -storepass Teknos01. -validity 360 -keysize 2048

3. Export client certificate:
   keytool -exportcert -alias client1 -keystore client1.p12 -file client1.cert

4. Export server certificate:
   keytool -exportcert -alias server -keystore server.p12 -file server.cert

5. Import client certificate into server keystore:
   keytool -importcert -file client1.cert -keystore server.p12 -alias client1

6. Import server certificate into client keystore:
   keytool -importcert -file server.cert -keystore client1.p12 -alias server

# Usage Notes
- Place server.p12 in the services/src/main/resources folder
- Place client1.p12 in the clients/console/src/main/resources folder
- Default password: Teknos01.