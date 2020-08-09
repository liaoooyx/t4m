# t4m project

T4M (Trends for Metrics) is a static code analysis tool to visualize the architectural metrics and trends for Java project, designing to be a complement of other similar tools in this domain like SonarQube, JDepend, etc.

It devotes to help developers to better understand the structure of their projects, to review whether the architecture of their project is evolving as expected and locate the deviations, as well as finding possible high-risk components that could be defective. 

## How to install

Download the source code to your computer or using: 

```
git clone https://github.com/liaoooyx/t4m.git
```

The executable jar file is placed in the `t4m/bin/` directory. 

## How to run

1. Prerequisite. Make sure you have Java 11 or higher in your OS.

   ```
   java -version
   ```

2. Preparation. Add the absolute root path of `t4m` root directory to your System Variable.

   - For Linux or Mac user: 

     Add the line below to your `~/.bash_profile` file. Assume the root path of `t4m` is `/absolute/path/to/t4m`.

     ```
     export T4M_HOME=/absolute/path/to/t4m
     ```

     Rememer to replace the root path of `t4m`.  You can get the absolute path by `cd` to the `t4m` root directory, and then execute command  `pwd`.

     Remember to run the command: `source .bash_profile` in order to load the variable to your OS.

     You can run the command `echo T4M_HOME` to check the system variable.

   - For Windows user: 

     Follow steps: `This PC -> Right Click -> Properties -> Advanced system settings -> Environment Variables -> System Variable -> New`. 

     ![windows-systeem-variable](doc/imgs/windows-systeem-variable.png)

     Save it and remember to restart your CMD. 

     You can run the command `set T4M_HOME` to check the system variable.

3. Start-up. Execute the `t4m-web-1.0-SNAPSHOT.jar` file which is placed in the `t4m/bin/` directory. 

   - For Linux or Mac user, assume the root path of `t4m` is `/absolute/path/to/t4m`.

     ```
     cd /absolute/path/to/t4m/bin
     java -jar t4m-web-1.0-SNAPSHOT.jar
     ```

   - For Windows user, assume the root path of `t4m` is `D:\absolute\path\to\t4m`.

     ```
     d:
     cd absolute\path\to\t4m\bin
     java -jar t4m-web-1.0-SNAPSHOT.jar
     ```

## How to use

After you run the `t4m-web` Jar file. It will start a web service for you. You can open the link below in your browser.

```
http://localhost:8087/
```

The detailed instructions of how to use will be shown on the web page.

## How to stop

Press `control + c` to stop.

## How to build

You may want to modify the source code and rebuild the project. We provide a gradle task for you to rebuild the project.

```
./gradlew buildJarToBin
```

It will build a executable jar file to `[t4mRootDir]/bin/` directory.

