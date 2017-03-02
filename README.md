# JUMO.world technical test


## Project details
This project was done in Scala it is a hybrid OOP and FP language but I lean more 
towards the FP side but incorporate OOP as well.

To compile assemble and run this jar file you will need the SimpleBuildTool http://www.scala-sbt.org.
You could make a standalone jar file by doing `sbt assemlby` it will create a jar file you could run. 
You could also just run the project via sbt with `sbt "run <input filename> [output filename]`. 

I have included in this project Haskell like IO Monad called Task for side effecting code like 
reading and writing to file or to the console. Task is not just just for side effects it is also
lazy evaluating and asynchronous by design so that we can run on separate threads for non blocking
operation. It also Trampolines the execution of the operations to stay stack safe regardless of the
complexity of the operations encapsulated with the algebraic data type.

I have also included a simplistic Functional library in FunctionalBoilerPlate.scala to allow me to use
Monads and enable the use of the Task Monad.

## Usage and assumptions
`usage: test <filename> [output filename]`

examples: 
* `java -jar test.jar Input.csv`
* `sbt "run somefolder/Input.csv"`

Assumptions I have made:
1. The input file format will not change this includes
* First line to contain the header
* Date format will stay the same
* Fields will stay in the same order
* No extra fields being added or fields being removed
2. The files are not hundreds of megs in size
* It is easy to adjust the code to be able to handle bigger files but because i wanted to 
showcase the versatility of my codebase and i have time constraints not only the 72 hour constraint but 
i have several projects that i have to complete before i can leave this position
* It will still work the performance will just be sub par
3. The Output file format is going to stay the same
* Both the fields and order in which they appear and the fact that there is going to be a header line

## Choices around the language
I chose Scala as it is not only the language that I have been using most recently but it is also really 
easy to get complex business rules written in a lot less lines of code. As you will see that i not only 
wrote the business rules but also simplistic Functional and Lazy/Async with Trampolining so that i wont
run into stack overflows for large amounts of operations that are lazy.

### This has been implemented in the way i would do for a production system 