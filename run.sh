rm -rf app/book.db && ./gradlew clean && ./gradlew run --args="../test1.txt"; xxd -R always app/book.db
