# t4m project

T4M (Trends for Metrics) is a static code analysis tool to visualize the architectural metrics and trends for Java project, designing to be a complement of other similar tools in this domain like SonarQube, JDepend, etc.

It devotes to help developers to better understand the structure of their projects, to review whether the architecture of their project is evolving as expected and locate the deviations, as well as finding possible high-risk components that could be defective. 

## How to install

Download the source code to your computer or using: 

```
git clone https://github.com/liaoooyx/t4m.git
```

The executable jar file is placed in the `t4m/run/` directory. 

## How to run

1. Before run the service, you need to the add the absolute root path of `t4m` root directory to your System Variable.

   - For Linux or Mac user: 

     Add the line below to your `~/.bash_profile` file.

     ```
     export T4M_HOME=/absolute/path/to/t4m
     ```

     Rememer to replace the root path of `t4m`.  You can get the absolute path by `cd` to the `t4m` root directory, and then execute command  `pwd`.

     Remember to run the command: `source .bash_profile` in order to load the variable to your OS.

   - For Windows user: 

     Follow steps: `This PC -> Right Click -> Properties -> Advanced system settings -> Environment Variables -> System Variable -> New`.

     ![windows-systeem-variable](doc/imgs/windows-systeem-variable.png)

2. Go to the `t4m` root dir. Run the following command:

   ```
   java -jar run/t4m-web-1.0-SNAPSHOT.jar
   ```

## How to use

After you run the `t4m-web` Jar file. It will start a web service. You can open

```
http://localhost:8087/
```

 in your browser and follow the instructions.

## How to stop

Press `control + c` to stop.

## How to build

You may want to modified the source code and rebuild the project. We provide a gradle task for you to build the project.

```
./gradlew buildT4M
```

It will build the the executable jar file to `[t4mRootDir]/run/` 