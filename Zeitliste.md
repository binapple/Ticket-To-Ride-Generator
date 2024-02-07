# Project Time Tracking

[Back to Start](README.md)

## April 2023
 
monatliche Arbeitszeit: 12h 32m

**12.04.2023 16:13 - 17:44; (1h 31m)**
- Zeitplanung erstellen;

**21.04.2023 20:57 - 23:43; (2h 46m)**
- Ticket To Ride Europe - Spielbrettstatistiken;

**23.04.2023 12:32 - 13:30; (0h 58m)**
- Recherche APIs OSM -> Overpass;
- Recherche welche Programmiersprache verwendet werden sollte;

**24.04.2023 18:45 - 19:30; (0h 45m)**
- Recherche Bilder zusammenführen -> noch keine gute Lösung gefunden;

**25.04.2023 17:45 - 18:30; (0h 45m)**
- Recherche für README-Templates -> Best-README-Template;
- Recherche Bilder zusammenführen -> Python & svg;

**26.04.2023 13:30 - 14:20; (0h 50m)**
- Konzepterstellung Beginn;

**27.04.2023 13:15 - 17:12; (3h 57m)**
- Konzept Fertigstellung;
- Statusbericht;

## Mai 2023

monatliche Arbeitszeit: 19h 13m

**10.05.2023 10:37 - 11:40; (1h 3m)**
- Setup für Projekt und Entwicklungsumgebung;

**11.05.2023 10:32 - 16:58; (6h 26m)**
- Einbindung OSM in Angular mittels Leaflet/Leaflet-ngx;
- Festlegung der Ratio der Karte;
- Interaktives Aussuchen des Kartenbereichs;

**13.05.2023 11:38 - 16:05; (4h 27m)**
- Auslesen von Koordinatensystem (Eckpunkte der Karte);

**22.05.2023 10:02 - 11:51; (1h 49m)**
- Github Account einrichten;
- Repository erstellen;

**24.05.2023 09:15 - 10:42; 15:52 - 18:43; (5h 18m)**
- Grundlegenden "Durchschnitt" durch das Backend erstellen (JPA, Tests, Repositories, Services, Plugins);

**28.05.2023 19:50 - 21:00; (1h 10m)**
- Dependency Bug Suche in Spring;

## Juni 2023

monatliche Arbeitszeit: 23h 48m

**13.06.2023 14:02 - 18:36; (4h 34m)**
- Overpass API call + Query für Städteliste ins Backend einbauen;

**14.06.2023 16:03 - 19:14; (3h 11m)**
- Städte abspeichern mit ihren Koordinaten und Einwohnerzahlen;

**19.06.2023 16:46 - 18:44; (1h 58m)**
- Mehrere Maps können nun die gleichen Städte beinhalten, ohne diese doppelt zu speichern;
- Städte in absteigender Population zurückgegeben;

**21.06.2023 10:26 - 14:09; 15:30 - 16:16; (4h 29m)**
- Map Speicherung und Laden von Städten für diese Map in frontend einbauen;
- Initiale Liste von Städten präsentieren im frontend;

**22.06.2023 12:05 - 13:50; (1h 45m)**
- Städte auf der frontend map anzeigen lassen;

**23.06.2023 18:13 - 19:41; (1h 28m)**
- Städte bearbeiten im Frontend (hinzufügen, weglassen);

**24.06.2023 11:44 - 15:37; (3h 53m)**
- Städte per Doubleclick auf der Map entfernen;
- Suche für nicht hinzugefügte Städte;
- kleinere Städte (town in OSM) nach Wunsch zusätzlich laden backend und frontend;

**28.06.2023 16:50 - 19:10; (2h 20m)**
- Erste Schritte für den Zugverbindungsalgorithmus im backend;

## Juli 2023

monatliche Arbeitszeit: 18h 30m

**01.07.2023 12:51 - 13:15; (0h 24m)**
- Neuer Endpoint und Test abstimmen;

**10.07.2023 09:55 - 15:34; (5h 39m)**
- Basics für den Zugverbindungs-/Graphen-Algorithmus;

**14.07.2023 09:36 - 12:32; 14:42 - 15:04; (3h 18m)**
- Ausfiltern der zu langen Verbindungen;

**15.07.2023 12:45 - 13:40; 14:28 - 15:10; (1h 37m)**
- Anzeigen von connectivityIssues (zu lange Abstände zwischen Städten);

**16.07.2023 13:36 - 17:03; (3h 27m)**
- Anzeige von Zugverbindungsvorschlag während der Städte-Wahl mit Highlighting von connectivity Issues (zu lange Strecken zwischen Städten);

**25.07.2023 17:43 - 19:53; (2h 10m)**
- Streckenlängen-Filterung erweitert;
- Im Frontend anzeigen von nicht ausgewählten Städten, per Doppel-Klick selektieren;

**29.07.2023 08:44 - 11:59; (3h 15m)**
- Streckenvorschlag auf planare Ergebnisse erweitert;

## August 2023

monatliche Arbeitszeit: 34h 23m

**05.08.2023 09:39 - 13:36; (3h 57m)**
- Anfänge für den Einfärbungsalgorithmus;
- Abspeichern von MapPoints im Backend;

**10.08.2023 16:33 - 19:47; (3h 14m)**
- Rework der Map Speicherung (frontend & backend) angefangen;
- MapPoints Abspeicherung fertig;

**13.08.2023 15:15 - 18:03; (2h 48m)**
- Map Speicherung reworked;
- Colorization Visualisierung angefangen;

**14.08.2023 10:20 - 12:17; 15:56 - 16:54; (2h 55m)**
- Colorization Algorithmus weitergebaut;
- Frontend bereit gemacht für Colorization-Darstellung

**15.08.2023 09:18 - 12:51; (3h 33m)**
- Weiterarbeiten am Colorization Algorithmus;

**19.08.2023 09:39 - 15:42; (6h 3m)**
- Bug Fixing beim Speichern von MapPoints/Colorization-Algorithmus;

**20.08.2023 11:20 - 12:30; 14:51 - 16:10; (2h 29m)**
- Weiterarbeiten am Colorization-Algorithmus (neue Ansätze);

**28.08.2023 10:52 - 12:15; 15:02 - 19:50; (6h 11m)**
- Kleinen Fehler in reduceEdges behoben (länger Strecken 8er, 2 6er richtig behalten);
- Colorization überarbeitet (wirklich unique Farben pro Stadt garantiert);
- Tunnelverbindungen auf den Farbfeldern aufgeteilt;

**30.08.2023 16:03 - 20:56; (4h 53m)**
- Tunnelfelder und Jokerfelder eingefügt bei farblosen Verbindungen;
- Darstellung von Jokerfeldern im Frontend;

## September 2023

monatliche Arbeitszeit: 8h 29m

**10.09.2023 15:13 - 19-17; (3h 4m)**
- Städteradius hinzugefügt -> Verbindungen starten nun ab einer gewissen Distanz zum Stadt-Ursprung;
- README Template verwendet & Teile bearbeitet;

**17.09.2023 09:55 - 13:09; (3h 14m)**
- Farbduplikationen gefixt;
- Tunnel und Joker können nun erst ab Länge 2 bei farblosen Verbindungen aufscheinen;
- Kommentare zu MapServiceImpl hinzugefügt;
- Initiale Testdaten eingefügt (Europa Ticket-To-Ride);

**27.09.2023 09:39 - 11:50; (2h 11m)**
- Anfänge für Editierung von Map Points erstellt;

## Oktober 2023

monatliche Arbeitszeit: 42h 21m

**05.10.2023 12:50 - 13:14; 15:12 - 16:57; (2h 9m)**
- MapPoint-Update-EndPoint hinzugefügt;
- Update mit Logik versehen (z.B. Farbe für ganze Connection ändern usw.);
- Frontend Anfänge für Updates;

**09.10.2023 15:31 - 16:46; (1h 15m)**
- Frontend id Weitergabe über Leaflet Objekt;
- Anfänge Edit Modal;

**10.10.2023 14:44 - 17:01; 18:44 - 19:53; (3h 26m)**
- Edit Modal eingefügt; erste Editierung von MapPoints fertiggestellt;

**11.10.2023 16:31 - 18:35; (2h 4m)**
- Erstellen von neuen Verbindungen durch bestehende "City" MapPoints im Backend ermöglicht;
- Anfänge für Verbindungs-Löschung;

**13.10.2023 12:02 - 16:44; (4h 42m)**
- Edit-Modal Löschung von Verbindungen hinzugefügt;
- Im Frontend hinzufügen von neuen Verbindungen ermöglicht;
- Überarbeiten des Streckenvorschlags für 8er und 6er Verbindungen;

**17.10.2023 14:08 - 16:36; 17:10 - 19:00; 19:38 - 20:32; (5h 12m)**
- Vektorgrafiken der Züge erstellen;

**18.10.2023 14:46 - 18:05; (3h 19m)**
- Anfänge für den Spielbrett-Algorithmus;
- Apache Batik einlesen;

**20.10.2023 11:23 - 13:12; (1h 49m)**
- SVG Validation;

**23.10.2023 12:08 - 16:50; (4h 42m)**
- SVGs Zusammenfügen mittels Apache Batik (noch nicht ganz fertig);

**24.10.2023 13:45 - 18:03; 18:35 - 21:24; (7h 7m)**
- SVG revalidieren;
- Spielbrett-Algorithmus anpassen/korrigieren;
- Color und Connection-Algorithmus Distanz-Bugs fixen;

**27.10.2023 12:28 - 17:20; (4h 52m)**
- Spielbrett-Algorithmus grob fertiggestellt;
- weitere Fehlerbehebungen;

**30.10.2023 13:47 - 15:20; (1h 33m)**
- Rendering von OSM Recherche;

## November 2023

monatliche Arbeitszeit: 56h 41m

**06.11.2023 11:48 - 14:51; (3h 3m)**
- Mapnik/printmaps/myosmatik installation Recherche;

**07.11.2023 15:20 - 17:54; (2h 34m)**
- Alternativen suchen für rendering;

**09.11.2023 11:59 - 16:46; (4h 47m)**
- Suche nach Karten-Rendering/Image-Export Software;
- Maperitive scripting;

**13.11.2023 12:23 - 16:49; (4h 26m)**
- Maperitive einbauen und automatisieren;
- Zusammenfügen von SVG und gerendertem Bild;

**14.11.2023 18:30 - 20:10; (1h 40m)**
- PDF Konvertierung Anfänge;

**15.11.2023 16:32 - 19:53; (3h 21m)**
- PDF Konvertierung fertig;

**16.11.2023 13:21 - 22:09; (8h 48m)**
- Ticketerstellung Anfänge;

**20.11.2023 13:27 - 14:43; (1h 16m)**
- SVG Formatierungs-Versuche (optimierung der Strecken SVGs);

**23.11.2023 11:03 - 16:58; (5h 55m)**
- Kreise für die Auftragskarten hinzufügen;
- Auftragstickets fertig;

**27.11.2023 10:10 - 11:43; 12:40 - 14:04; (2h 57m)**
- Files in Persistence speichern;
- erste Schritte Frontend für PDF Präsentierung;

**28.11.2023 10:16 - 12:48; 13:31 - 17:10; (6h 11m)**
- Bug fix in gameBoard (nicht geplante updates auf MapPoints wegen direkter Referenzierung auf Nachbarn);
- Anzeigen von GameBoard und TicketCards im Frontend;

**29.11.2023 12:50 - 14:54; 16:22 - 18:48; (4h 30m)**
- Frontend design überarbeiten; PDF Darstellung fixen (zu große Dateien werden nicht angezeigt);

**30.11.2023 13:31 - 14:50; 16:08 - 17:12; (2h 23m)**
- PDFs auch in neuem Fenster öffnen lassen (für manche Browser bei zu großen PDFs);
- README.md Anfänge;

## Dezember 2023

monatliche Arbeitszeit: 27h 35m

**07.12.2023 13:28 - 16:49; (3h 21m)**
- Map Points "draggable" für Repositionierung machen;

**08.12.2023 14:40 - 16:25; (1h 45m)**
- Versuch die Verschiebung der MapPoints zu fixen;

**11.12.2023 15:54 - 16:46; (0h 52m)**
- Mercator Projection anwenden = Fix für Verschiebung;

**12.12.2023 16:29 - 19:53; (3h 23m)**
- Offset fixes bei Ticket-Cards;

**13.12.2023 14:52 - 16:47; (1h 55m)**
- Building von frontend und backend für testsystem;

**14.12.2023 14:29 - 17:07; (2h 38m)**
- Versuch die missing fonts zu fixen;

**19.12.2023 16:22 - 23:50; (7h 28m)**
- Missing fonts fixen;
- Frontend Schritt zurück einfügen;
- Backend build fixes (FOP file classpath ready machen, maven encoding);

**28.12.2023 15:25 - 18:27; (3h 2m)**
- Testsystem Google Cloud Platform Anfänge (Files uploaden, Installationen);

**29.12.2023 16:38 - 18:08; 19:24 - 20:55; (3h 1m)**
- Backend auf dem Testserver zum Laufen bringen (Kommunikation mit frontend herstellen)

## Januar 2024

monatliche Arbeitszeit: 40h 51m

**02.01.2024 15:57 - 17:28; 21:22 - 00:32; (4h 41m)**
- Versuch Maperitive auf Linux zum Laufen bringen;
- Xvfb Installation;

**04.01.2024 11:50 - 16:20; (4h 30m)**
- Rechteproblem auf Testserver fixen (Maperitive von Java aus starten);
- Programm als system service definieren;

**10.01.2024 15:00 - 16:34; (1h 34m)**
- Städte löschen in Editier-Ansicht;

**15.01.2024 13:22 - 17:01; (3h 39m)**
- Städte löschen überarbeitet;
- Frontend-Hinweise ergänzt;
- Veränderungen basierend auf DPI - Anfänge;

**16.01.2024 15:08 - 16:02; 17:29 - 18:27; (1h 52m)**
- SVG Logik reworken (an DPI anpassen);

**17.01.2024 15:32 - 19:08; (3h 36m)**
- SVG Logik überarbeiten;

**18.01.2024 13:37 - 17:21; (3h 44m)**
- DPI Bilder rendern;

**20.01.2024 10:41 - 13:15; 15:28 - 16:27; 17:20 - 19:15; (5h 28m)**
- Memory leaks fixen;
- PDF in eigenes Entity speichern für lazy loading;
- Offsets fixen;

**23.01.2024 14:25 - 19:27; (5h 2m)**
- Offsets fixen für Zugstrecken & rote Kreise auf Tickets;
- Ticket-Reihenfolge überarbeiten;
- OS-dependent Methoden-Aufrufe;

**24.01.2024 17:53 - 22:23; (4h 30m)**
- Server Speicher, Arbeitsspeicher und CPU anpassen; Testen am Server;

**26.01.2024 16:55 - 17:43; (0h 48m)**
- Städte anordnen und von Backend laden bei Schritt-zurück;

**31.01.2024 14:28 - 15:45; (1h 17m)**
- Verschiedene DIN-A-Formate testen (A0, A1, A2 gute Ergebnisse - A3, A4 eher nicht spielbar);

total bis inklusive Januar 2024: 317h 20m

## Ferbruar 2024

monatliche Arbeitszeit: 

**01.02.2024 11:38 - 17:14; (5h 36m)**
- DIN-A-Formate, DPI, Bearbeitungsstatus und Name in Map mitspeichern und im Frontend einbauen;
- Anfänge für Map-Liste;

**02.02.2024 11:52 - 15:27; (3h 35m)**
- Map-Liste besser gestalten;
- Map-Zoom responsive machen; 