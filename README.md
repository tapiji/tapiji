
# <a href="http://tapiji.github.io/"  target="_blank">TapiJI</a> (Tooling for Agile and Process Integrated Java Internationalization)

## Introduction

Since the first steps of the Java, language-integrated Internationalization assistance has been considered as a basic requirement. Nowadays, Java programs can be easily prepared for supporting multiple output languages by using Java provided Internationalization mechanisms. But in daily development, the corresponding tasks can be very tedious, which often leads to inconsistent and incomplete Internationalizations.  The primary goal of the TapiJI project is to assist developers for making Java Internationalization more convenient for real live software projects. This is accomplished with the help of tooling support on different stages in development. 

TapiJI Translator is available for three different Eclipse-Platforms:
 * Eclipse Plugin
 * Rich Client Appication (RCP)
 * Remote Application Platform (RAP)

Technical background please see [Wiki](https://github.com/tapiji/tapiji/wiki)

##The basic architecture of TapiJI is separated into three components:
#### Resource-Bundle editor
The Resource-Bundle editor is a central UI component that is shared by other plug-ins. It is based on the work of [Pascal Essiembre](https://github.com/essiembre/eclipse-rbe) and allows editing multiple property files of a Resource-Bundle like a single resource under modification.

#### TapiJI Glossary
Professional translators are assisted by a translation glossary that provides:
 * Hierarchical term definitions
 * Automatically locating terms that are currently edited within the Resource-Bundle editor
 * Re-organizing term hierarchies
 * XML export 
 
#### TapiJI Translator
The TapiJI Translator represents a stand-alone application for translating program resources. It is built atop of the Eclipse Rich Client Platform (RCP) and Remote Application Platform (RAP). It allows to directly open Java Resource-Bundles in the Editor.

![TapiJI Resource Bundle Editor](http://tapiji.github.io/images/screenshots/workbench.png)

### Downloads

#### Download 1.0.0-Snapshot Pre Release
**Linux:**
[Tapiji Translator 32Bit](https://github.com/tapiji/tapiji/releases/download/1.0.0-SNAPSHOT/tapiji_translator-linux.gtk.x86.zip)  
[Tapiji Translator 64Bit](https://github.com/tapiji/tapiji/releases/download/1.0.0-SNAPSHOT/tapiji_translator-linux.gtk.x86_64.zip)  
 
**Windows:**
[Tapiji Translator 32Bit](https://github.com/tapiji/tapiji/releases/download/1.0.0-SNAPSHOT/tapiji_translator-win32.win32.x86.zip)  
[Tapiji Translator 64Bit](https://github.com/tapiji/tapiji/releases/download/1.0.0-SNAPSHOT/tapiji_translator-win32.win32.x86_64.zip)  
