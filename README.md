# Skalierbarer Git-Client
![Jenkins](https://img.shields.io/jenkins/build?jobUrl=https%3A%2F%2Fjenkins.tobiasmanske.de%2Fjob%2FSkalierbarer%2520Git-Client%2F&style=flat-square)
![Jenkins Coverage](https://img.shields.io/jenkins/coverage/jacoco?jobUrl=https%3A%2F%2Fjenkins.tobiasmanske.de%2Fjob%2FSkalierbarer%2520Git-Client%2F&style=flat-square)
[![GitHub license](https://img.shields.io/github/license/osl2/Skalierbarer-Git-Client?style=flat-square)](https://github.com/osl2/Skalierbarer-Git-Client/blob/master/LICENSE)


Der skalierbare Git-Client ist mit dem Ziel entstanden, einen grafischen Git-Client zu erstellen, welcher Git stufenweise
einführt. Hierzu unterstützt dieses Projekt die meist genutzten Git-Kommandos, in aufeinander aufbauende Stufen gegliedert.  

Ein weiterer Fokus lag auf der Erweiterbarkeit und der Austauschbarkeit der einzelnen Komponenten.
Somit sollte es ohne großen Aufwand möglich sein, weitere Funktionalität hinzuzufügen, oder einzelne Komponenten auszutauschen.

## Setup
Um den Git-Client verwenden zu können, muss dieser zuerst kompilliert werden. Dies geschieht mit Hilfe von [Apache Maven](https://maven.apache.org/).

```bash
mvn package
```

Die resultierende Jar-Datei im `target/`-Verzeichnis lässt sich dann ausführen.

## Kompetenzstufen anpassen

Beim Start sucht der Client nach den Dateien `config/data.json` und `config/settings.json`.  
Diese enthalten Daten über die zuletzt verwendeten, lokalen Repositories und Programmeinstellungen.  

In der Datei `data.json` befindet sich eine `levels`-Datenstruktur. Um einen Git-Befehl zu einer Kompetenzstufe hinzuzufügen, muss dessen Classpath in diese eingetragen werden. Alle Klassen in `src/main/java/commands` sind valide Kommandoklassen, welche hier eingetragen werden können.
```json
 "levels" : [ {
    "name" : "Level 1",
    "commands" : [ {
      "classPath" : "commands.Add"
    }, {
      "classPath" : "commands.Commit"
    }, {
      "classPath" : "commands.Init"
    }, {
      "classPath" : "commands.Revert"
    } ],
    "id" : 1
  },
  ...
  ]
```
