# City simulator application

The repository contains all the work done to design and next develop the **city simulator** web application. The project was born with the initial purpose of participating in final examination of [Concurrent and Distributed Systems](http://informatica.math.unipd.it/laureamagistrale/sistemiconcorrentiedistribuiti.html) class followed during the last year at Università degli Studi di Padova at department of mathematics [Master of Science (MSc) in computer science](http://informatica.math.unipd.it/laureamagistrale/index.html)

The project is designed and developed during the academic year 2014/15 by [Andrea Meneghinello](mailto:meneghinello.andrea@gmail.com).

#### Table of contents

  - [Project license](#markdown-header-project-license)
  - [Project description](#markdown-header-project-description)
  - [Features available](#markdown-header-features-available)

---

## Project license

The project is distributed under the [GNU General Public License (version 2.0)](http://www.gnu.org/licenses/old-licenses/gpl-2.0.html).

A copy of the license, with right and duties, can be found in the [License.txt](License.txt) file contained in the root directory of this repository.

[back to menu](#markdown-header-table-of-contents)

---

## Project description

The project's main goal is to acquire the skills of resolve, a given problem, in a  **concurrent and distributed** way.

The problem to deal with consists in simulate people's life inside a city defined by the system owner. The above city contains people (citizen) that do a very simpler life, in particular they:

  - wake up at home
  - travel to reach the work place
  - work for some amount of time
  - travel to reach the home place
  - sleep for some amount of time

Every citizen adopt this life behavior incessantly until the system is closed.

In order to participate in final examination the candidate must resolve the following skills:

  - **design** a **concurrent and distributed** system to solve the problem
  - **develop** the designed system with technologies chosen by the candidate
  - produce a **report** that illustrates:
    - the problem addressed
    - the **design choices** adopted during the design phase
    - the **reasons** that lead to choice the **technologies** used to develop the system

[back to menu](#markdown-header-table-of-contents)

---

## Features available

The actual software version (1.0) is the one that is submitted to the examiner and contains the following features:

 - city configuration via [XML Language](https://en.wikipedia.org/wiki/XML)
 - check the configuration file via an [XML Schema](https://en.wikipedia.org/wiki/XML_Schema) grammar during the bootstrap phase of the system
 - available vehicles: bus - car - walkers
 - each citizen can use only the assigned vehicle all the time
 - route finding through the [AODV Routing Algorithm](https://en.wikipedia.org/wiki/Ad_hoc_On-Demand_Distance_Vector_Routing)
 - previously of vehicles management at the crossroads according to the highway code
 - [Graphic User Interface (GUI)](https://en.wikipedia.org/wiki/Graphical_user_interface) in an [HTML5 Canvas](https://en.wikipedia.org/wiki/Canvas_element)
 - [Graphic User Interface (GUI)](https://en.wikipedia.org/wiki/Graphical_user_interface) connected to the system core modules through [WebSocket](https://en.wikipedia.org/wiki/WebSocket)

[back to menu](#markdown-header-table-of-contents)

---