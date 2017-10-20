# Ohmage-omh Platform

From the Small Data Lab @ CornellTech

This repository holds the majority of the code base for the Ohmage-omh platform, along with instructions for setting up your own instance of it.  For more information about what the Ohmage-omh platform is, view the [main website here](http://smalldata.io/ohmage-omh-website/), and you can learn more about the Small Data Lab [here](http://smalldata.io).

This project builds on [Open mHealth's](http://www.openmhealth.org/) Java implementation of their Data Point API, and you can view that project and documenation [here](https://github.com/openmhealth/omh-dsu-ri).  

In addition to their "Authorization API" and "Resource API", the Ohmage-omh platform adds:

 - a "Study Manager Portal" to view and manage participants, apps, and data for each study, and
 - a "Data Processing Unit", which is a server that process raw data in the background to compute higher level metrics.

## Installation

To run your own instance of the Ohmage-omh platform on a physical or virtual machine, we provide a simplified deployment configuration that uses [Docker containers](https://www.docker.com/whatisdocker).  We chose Docker because the Ohmage-omh platform includes several interdependent servers, and Docker provides an easy way to deploy and interface those components without requiring knowledge of the underlying technologies used to build them.

Below is a list of all the components that you will install for the platform, along with a link if the component is not in this repository.

- [MongoDB](https://www.mongodb.org/)
- [PostgreSQL](http://www.postgresql.org/)
- Ohmage Authorization Server (`authorization-server` directory)
- Ohmage Resource Server (`resource-server` directory)
- [Ohmage Shim Server](https://github.com/smalldatalab/omh-shims)
- Ohmage Manager Portal (`ohmageomh-manage-server` direcotyr)
- Ohmage Data Processing Unit (`ohmageomh-dpu-server` directory)

At a high level, the setup process will be:

1. (Optional) Create "client" accounts with 3rd party services that you'll use (e.g. Google, Fitbit, Moves)
1. Edit the `docker-compose.yml` file for your environment.
1. Setup your machine and install Docker.
1. Transfer the `docker-compose.yml` file to the machine and run it.
1. Initialize the databases, and restart.


## Step 1: Create client accounts with 3rd party services

`NOTE:` It is possible to setup and run the Ohmage suite without creating any of the 3rd party accounts listed below, and you can always come back later and add integration with these services. If you want to run Ohmage without these 3rd party services, skip to Step 2.

#### 3rd Party - Google Client ID

Currently, the Ohmage suite allows users to login using their Google account.  To enable this, you will register your instance of the suite with Google, so they can provide the authorization between the user and your system.  When you have done this, Google will assign your system a 'clientId' and 'clientSecret' which you will use for credentials.

To start you will need to have a project on the Google APIs Console (you can create a new one or add to an existing one).  You can find more information about that [here](https://developers.google.com/console/help/)  To add a Client ID within the project, navigate to the project console, and on the left-hand menu select APIs & auth > Credentials.  Select "Create a new Client ID".  Enter the information and create the ID.  NOTE: You must have a domain name for your server, because Google APIs does not accept raw IP addresses as redirects.

NOTE: You MUST enable GOOGLE PLUS API!

Record your `clientId` and `clientSecret` for later.   


#### 3rd Party - Fitbit API

If you wish to use Fitbit data, you will need to create a developer account with them and register your instance of the suite to get a 'clientId' and 'clientSecret'.  If you do not wish to use Fitbit data, you can skip this, and additionally exclude those parameters when starting the shim Docker container, below.  (i.e. remove '-e openmhealth.shim.fitbit.clientId=...' and '-e openmhealth.shim.fitbit.clientSecret=...' when starting the container).

To get started, go [here](https://dev.fitbit.com) and create a developer account.  Once you logged into the developer account, select "Register an App", which takes you [here](https://dev.fitbit.com/apps/new).  Enter the information for your instance of the suite.  Use the following settings:

```
Application Type = Browser
Callback URL = {YOUR BASE DOMAIN URL}/shim/authorize/fitbit/callback
Default Access Type = Read-Only
```

Once created, record the `OAuth 2.0 Client ID` and `Client Secret` for later.

## Step 2: Edit the 'docker-compose' file
Docker Compose is a tool for configuring multiple Docker containers with a single script.  When creating and starting all the containers, you will specify a single 'docker-compose.yml' file to automate the setup.

The main template for the `docker-compose.yml` file is in this repository in the /docker directory, named `docker-compose.SAMPLE.yml`.  You can make a copy of this file, and name it `docker-compose.yml`.

If you are setting up the a local version of Ohmage, you can use the file as is.  If you are running on a remote server, you will want to update the variable values that are in the file, indicated with "{}" brackets. For the `{BASE URL}`, include the "http://" at the beginning.  For any of the services you are not using (including Google Signin and Mandrill), you can just leave the variables as is, without editing them.

## Step 3: Setup the machine and install Docker

Our testing was done on an AWS instance, with their Ubuntu 14.04.2 distro. However, the suite should run fine on any OS that is supported by Docker.  The rest of the instructions assume you are able to access a terminal on your host machine (ssh).

NOTE: On VM hosts such as AWS, port 80 is not open, by default.  Through the AWS Console, you can open port 80 by following the steps [here](http://stackoverflow.com/questions/5004159/opening-port-80-ec2-amazon-web-services).

Once your machine is setup, you can find instructions to install Docker Compose for your OS [here](https://docs.docker.com/compose/install/) (This includes instructions for installing Docker Engine, too).

NOTE: If you get a `Permission denied` error when trying to install via curl, run `sudo -i` first.

## Step 4: Transfer your docker-compose.yml and nginx.conf file

Somehow, you need to get your modified `docker-compose.yml` and the `nginx.conf` files to the server.

If you are using Linux, cd to this directory, and you can transfer the file with something like:
```
scp docker/docker-compose.yml ubuntu@{BASE URL}:~
scp nginx/nginx.conf ubuntu@{BASE URL}:~
```

Once the files are transferred, ssh into the machine, and you can move it to a sub-directory with:
```
cd ~
mkdir omh
mv docker-compose.yml omh
cd omh
```
NOTE: We move the file to the `omh` directory to give each Docker container a consistent naming prefix, because it by default includes the current directory name.


## Step 5: Initialize the databases and restart

We use Docker volume containers to store the database data. To initialize these, run the commands specified in the top comments of the sample `docker-compose.yml` file, to create the `mongodata` and `postgresdata` containers.

Once the volumes are created, the Mongo database is ready to go, but the Postgres database needs to be initialized by adding a few tables.  To add those tables, complete the following steps:

1. Start the Postgres container with `sudo docker-compose up -d postgres`.
1. Run `sudo docker exec -it omh_postgres_1 bash` to start a shell on the `postgres` container
1. Run `psql -U postgres` in the resulting shell to start `psql`
1. Run `CREATE DATABASE omh`
1. Copy and paste the contents of the [database setup script](https://github.com/smalldatalab/omh-dsu/blob/master/resources/rdbms/postgresql/oauth2-ddl.sql) to create the tables.
1. Copy and paste the contents of the [client initialization script](https://github.com/smalldatalab/omh-dsu/blob/master/resources/rdbms/postgresql/initialize-oauth-clients.sql) to add the client details for the various apps and 3rd party integrations.
1. `\q` to exit `psql`
1. `exit` to exit the shell

Once the databases are ready, you can simply start all the containers and they will be ready to go.  Start all containers with:
```
sudo docker-compose up -d

# Stop all containers with
sudo docker-compose stop
```

At this point, you should be ready to go.  You can login as sample users with the info below, or follow the "Getting Started" instructions in the section below.

Role | URL | Username | password
--- | --- | --- | ---
Study Manager | {BASE URL}/manage | manager1 | manager1
System Admin | {BASE URL}/manage | admin | admin

# Getting Started

Once you have installed the platform, you can follow these instructions to login as the various user roles.

## Sign in as a Study Manager

1. In a browser, navigate to `{BASE URL}/manage`, and login with the sample Study Manager account info in the table above.
1. The first page shows a list of studies this manager has access to. Click on "Example Study 1".
1. This page shows a list of participants who are enrolled in the study. You'll notice two sample participants have been automatically created.
1. On the right side is a list of the "Apps" that are enabled for the study, along with the data types that are collected for each.  For the example study, all possible Apps have been enabled.  Under that list, you'll see a list of the Surveys that are enabled for this study.  These are the surveys a participant will see if they install and login to the Ohmage-omh mobile survey app.
1. Click on a participant in the table. If there was data for that participant, you will see points on the calendar for each day that user has data.
1. Click on the "Browse Study Data" button in the upper right. Here you can view data in tablular format by selecting a data type and a participant.  To view Survey Responses collected from the Ohmage-omh mobile survey app, select the "Survey Responses" tab.
1. Go back the study page, and click the green "New Participant" button.  You'll see that a study manager can create a new participant account.  Take note that once you create the username and password for the participant to use when they login, you cannot retrieve that information, so make sure to write it down.
1. Create a new participant with
  - Label: Test 1
  - Login: test1
  - Password: test1  (or whatever you want)
1. You've now seen most of the Study Manager features. You'll now switch over to acting as a participant, to see how they would interact with the platform.

## Sign in as a participant

Participants can login to a homepage to view available apps and the studies they are enrolled in.

1. In a browser, navigate to the `{BASE URL}` and login with the sample participant info that you just created.
1. Scroll down, and notice the participant is already enrolled in the "Example Study 1".
1. On a mobile device, you can install any of the apps listed, and login with that same info. All data collected will be associated with this sample participant.
1. This is how any study participant would login to the apps, once a Study Manager has created login information for them.

## Sign in as the System Admin

The System Admin user is the top level user for the platform. They are responsible for creating and managing studies, study managers, and study configurations such as which Apps and Data Types are enabled, and which surveys are enabled.

1. In a browser, navigate to `{BASE URL}/manage` and login with System Admin account info in the table above.
1. View a list of all Study Manager accounts by clicking on Administration > User Management in the top menu bar.
1. View a list of other objects by selecting one under the Entities menu in the top menu bar.


# Logs
All of the containers will create log files for their server or database.  These files will reside within the containers (not on the host machine's fiel system), unless you update the docker-compose.yml configuration.

In the docker-compose-EXAMPLE.yml file provided, each service has a commented out `volumes` section.  You can uncomment these two lines for each, to write the log files to the host machine using a volume mount. The first part of the parameter (before the :) specifies the location on your host machine you want the parent log directory to be. Each container will create a sub-directory inside this location. The second part of the parameter (after the :) is the location inside the container that the log file is written to.  Edit the first part for your system, but leave the second part as is.

# Updating a Docker container
Once installation is completed, your Docker host will have copies of all the images from Docker Hub, and start and stop the containers as you wish.  However, if an updated release of one or multiple images is published to Docker Hub by SmallDataLab, your installation will not automatically update it's local images.

To update a local image to the latest build, you can follow these steps. This is an example for the `ohmage-auth-server` image, but you can substitute any other image and run the same steps.

IMPORTANT: This requires you to temporarily stop the application, so your users will experience downtime.

1. Connect to server in a terminal.
1. Navigate to the Docker Compose directory with `cd ~/omh`.
1. Stop ohmage-omh by running `sudo docker-compose stop`.
1. Fetch the latest build by running `sudo docker pull smalldatalab/ohmage-auth-server:latest`.
1. Restart the containers by running `sudo docker-compose up -d`.
