# jupyter-kernel-jshell

This is forked from fiber-space/jupyter-kernel-jsr223
The original jsr233 implementation was done by kay schluehr.

Currently its only supporting java via the JShell of JDK 9.

This project is a Java implementation of the Jupyter messaging protocol, 



## Getting started

### Building the project

After having cloned this project repository, switch to the project directory and build the project 
with ant

	~/home/my/repo/to/jupyter-kernel-jshell $ mvn clean package

	

The project directory should now contain a `target` folder showing the following content 
( modulo changes in version numbers ):

	target
	├── javadoc
	├── jupyter-kernel-jshell-1.0-SNAPSHOT.jar
	├── dependency
	│   ├── commons-cli-1.2.jar
	│   ├── jeromq-0.3.6.jar
	│   └── json.jar
	└── README.TXT

### Installing kernels and kernel specs

The jar file manifest expects the dependency folder to live beside the main jar file.


	kernelspec
	├── kernel.json
	└── README

Please open the README for further information.

After the classpath and the options have been properly set, create a new directory e.g.

	   home
	    └── my
			└── java_kernel
			    └── kernel.json

For kernel installation type

	$ jupyter kernelspec install /home/my/java_kernel

Finally control the kernel installation with	

	$ jupyter kernelspec list

## Using the kernel 

The jupyter-kernel-jhell library has been tested only with the Jupyter notebook.

