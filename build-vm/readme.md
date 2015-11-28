# VM setup using Vagrant
  1. Download and install [Vagrant](https://www.vagrantup.com/downloads.html)
  2. Download and install [VirtualBox](https://www.virtualbox.org/wiki/Downloads)
  3. Open up terminal or command prompt. Type in 'vagrant init rudolfochrist/ubuntu-desktop'. This will create a Vagrantfile.
  4. Open up the Vagrantfile and type in 'config.vm.provision :shell, path: "Installation.sh"' after the line containing config.vm.box="rudolfochrist/ubuntu-desktop".
  5. Make sure that the 'installation.sh' file is in the same folder as teh vagrantfile.
  6. Type in 'vagrant up --provider virtualbox' to start the machine.
  7. Following can be observed:
    * Base box image is downloaded and added in Vagrant.
    * Virtual machine is created and loaded using VirtualBox.
    * VM is launched in GUI mode.
    * Java 1.7 is downloaded and installed.
    * Apache Maven 3.3.9 is downloaded and installed.
    * Apache Tomcat 7.0.65 is downloaded and installed.
    * The tool TestEvol is set up.
    * Readme, License and Installation files are added.
    * Shortcut link to localhost:8080 is created.
    * Shortcut link to the video demonstration is created.
    * USB is disabled. 
    * The tomcate server is started.
  7. To start the tool, click on the link to localhost:8080 found on the desktop.

## Run the tool 
  * Follow the steps mentioned [here](https://github.com/SoftwareEngineeringToolDemos/ICSE-2013-TestEvol)

## Validation
  1. Open the terminal.
  2. Type `java -version`
    * Java version should be displayed.

## References
  1. [http://thediscoblog.com/blog/2013/11/18/provisioning-ubuntu-with-java-in-3-steps/](http://thediscoblog.com/blog/2013/11/18/provisioning-ubuntu-with-java-in-3-steps/)
  2. [https://docs.vagrantup.com/v2/getting-started/provisioning.html](https://docs.vagrantup.com/v2/getting-started/provisioning.html)
  3. [https://github.com/aglover/ubuntu-equip/raw/master/equip_java8.sh](https://github.com/aglover/ubuntu-equip/raw/master/equip_java8.sh)
