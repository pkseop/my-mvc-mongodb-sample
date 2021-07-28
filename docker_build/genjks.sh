#!/bin/sh

mydir=/usr/local/app
storepassword=changeit
cacerts=/opt/openjdk-14/lib/security/cacerts

curl -sS "https://s3.amazonaws.com/rds-downloads/rds-combined-ca-bundle.pem" > ${mydir}/rds-combined-ca-bundle.pem
awk 'split_after == 1 {n++;split_after=0} /-----END CERTIFICATE-----/ {split_after=1}{print > "rds-ca-" n ".pem"}' < ${mydir}/rds-combined-ca-bundle.pem

for CERT in rds-ca-*; do
  alias=$(openssl x509 -noout -text -in $CERT | perl -ne 'next unless /Subject:/; s/.*(CN=|CN = )//; print')
  echo "Importing $alias"
  /opt/openjdk-14/bin/keytool -import -file ${CERT} -alias "${alias}" -storepass ${storepassword} -keystore ${cacerts} -noprompt
  rm $CERT
done

rm ${mydir}/rds-combined-ca-bundle.pem\