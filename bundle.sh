rm -fr bin
mkdir bin
rm -r frontend/build/assets/*
npm run build:production --prefix frontend/
cp -fr frontend/build/assets bin/static
cp target/bank-1.0-SNAPSHOT.jar bin/bankApp.jar