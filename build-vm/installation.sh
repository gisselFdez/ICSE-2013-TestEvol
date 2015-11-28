# installing Java

sudo apt-get update
sudo apt-get install default-jre -y
sudo apt-get install default-jdk -y 
sudo update-alternatives --config java

# installing maven

cd /usr

sudo wget http://apache.mirrorcatalogs.com/maven/maven-3/3.3.9/binaries/apache-maven-3.3.9-bin.tar.gz

sudo tar -xvf apache-maven-3.3.9-bin.tar.gz 

# installing tomcat

cd /usr

sudo wget http://mirrors.koehn.com/apache/tomcat/tomcat-7/v7.0.65/bin/apache-tomcat-7.0.65.tar.gz

sudo tar -xvf apache-tomcat-7.0.65.tar.gz 

# changing the tomcat-users.xml file to give permisssions to use the GUI

sudo rm /usr/apache-tomcat-7.0.65/conf/tomcat-users.xml

sudo wget 'https://www.dropbox.com/s/wmcznso9au7xkhc/tomcat-users.xml?dl=1' -O /usr/apache-tomcat-7.0.65/conf/tomcat-users.xml

# changing the path variables 

JAVA_HOME=/usr/lib/jvm/java-7-openjdk-amd64
export JAVA_HOME
export PATH=${JAVA_HOME}/bin:${PATH}
M2_HOME=/usr/apache-maven-3.3.9
export M2_HOME
MAVEN_HOME=/usr/apache-maven-3.3.9
export MAVEN_HOME
export PATH=${MAVEN_HOME}/bin:${PATH}
CATALINA_HOME=/usr/apache-tomcat-7.0.65
export PATH=${CATALINA_HOME}/bin:${PATH}

# Downloading the config files

sudo wget --no-check-certificate 'https://drive.google.com/uc?export=download&id=0B0b70eAJZOJpcThxRERzOFJ5MlU' -O /home/vagrant/Downloads/config.tar.gz

tar -xvf /home/vagrant/Downloads/config.tar.gz 

# Adding the testevol.war file to the tomcat webapp

sudo wget 'https://www.dropbox.com/s/sbz96vm98cyyuqw/testevol.war?dl=1' -O /usr/apache-tomcat-7.0.65/webapps/testevol.war

# Adding project files

sudo wget --no-check-certificate 'https://www.dropbox.com/s/3derl01yyrj3fr9/projects.tar.gz?dl=1' -O /home/vagrant/Downloads/projects.tar.gz

cd /home/Vagrant/Downloads

sudo tar -xvf projects.tar.gz


# Adding the required files on desktop as per the rubric

sudo wget --no-check-certificate 'https://www.dropbox.com/s/o46hkjr30yhgdd5/TestEvol.tar.gz?dl=1' -O /home/vagrant/Desktop/TestEvol.tar.gz

cd /home/Vagrant/Desktop

sudo tar -xvf TestEvol.tar.gz

sudo rm /home/vagrant/Desktop/TestEvol.tar.gz

sudo wget --no-check-certificate 'https://www.dropbox.com/s/h0rw7443cctzgum/Install%20instructions.txt?dl=1' -O /home/vagrant/Desktop/InstallInstructions.txt

sudo wget --no-check-certificate 'https://www.dropbox.com/s/vx4t4clambuytg2/Licenses.txt?dl=1' -O /home/vagrant/Desktop/Licenses.txt

sudo wget --no-check-certificate 'https://www.dropbox.com/s/x089ad0jmsbkxlh/readme.txt?dl=1' -O /home/vagrant/Desktop/readme.txt

# Connecting to localhost:8080

sudo touch /home/vagrant/Desktop/LocalHost.desktop
echo -e "[Desktop Entry]\nEncoding=UTF-8\nName=Connect via LocalHost\nType=Link\nURL=http://localhost:8080" | sudo tee /home/vagrant/Desktop/LocalHost.desktop

# Creating the shortcut to the demo video

sudo touch /home/vagrant/Desktop/TestEvolDemo.desktop
echo -e "[Desktop Entry]\nEncoding=UTF-8\nName=Link to TestEvolDemo\nType=Link\nURL=https://www.youtube.com/watch?v=AT-cfe8j668\nIcon=text-html" | sudo tee /home/vagrant/Desktop/TestEvolDemo.desktop

# Launching tomcat server at startup

sudo xdg-open http://localhost:8080

# Removing the extra icons from the side bar

gsettings set com.canonical.Unity.Launcher favorites "['application://firefox.desktop']"

# Starting tomcat server

sudo /usr/apache-tomcat-7.0.65/bin/startup.sh




