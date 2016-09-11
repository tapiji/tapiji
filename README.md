# tapiji
<a href="https://travis-ci.org/tapiji/tapiji" target="_blank"><img src="https://travis-ci.org/tapiji/tapiji.svg?branch=R3_to_R4_migration"></a>

<a href="http://tapiji.github.io/">TapiJI Website</a>

## Introduction

Since the first steps of the Java, language-integrated Internationalization assistance has been considered as a basic requirement. Nowadays, Java programs can be easily prepared for supporting multiple output languages by using Java provided Internationalization mechanisms. But in daily development, the corresponding tasks can be very tedious, which often leads to inconsistent and incomplete Internationalizations.  The primary goal of the TapiJI project is to assist developers for making Java Internationalization more convenient for real live software projects. This is accomplished with the help of tooling support on different stages in development. 

The basic architecture of TapiJI is separated into three Eclipse plug-ins:
 * Resource-Bundle editor
 * [TapiJITools TapiJI Tools]
 * [TapiJITranslator TapiJI Translator]


### Resource-Bundle editor
The Resource-Bundle editor is a central UI component that is shared by both other plug-ins. It is based on the work of [http://sourceforge.net/users/essiembre Pascal Essiembre] and allows editing multiple property files of a Resource-Bundle like a single resource under modification. In the near future it is planned to reimplement this plug-in to get an editor that better integrates into new Eclipse versions.


