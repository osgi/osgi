MAVEN_USER=user
MAVEN_PASSWORD=password
PGP_PASSWORD=password

core-to-maven :
	bnd -exceptions -trace maven bundle \
		-passphrase $PGP_PASSWORD\
		-output maven.osgi.core.jar \
		-javadoc http://www.osgi.org/download/r4v43/r4v43.javadoc.zip \
		-url http://www.osgi.org \
		-nodelete \
		-group  org.osgi \
		-artifact org.osgi.core \
		http://www.osgi.org/download/r4v43/osgi.core.jar
	curl -u $MAVEN_USER:$MAVEN_PASSWORD https://oss.sonatype.org/service/local/staging/bundle_upload -F "file=@maven.osgi.core.jar;filename=core.jar"


