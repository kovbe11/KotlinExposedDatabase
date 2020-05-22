a DBCreator.kt-ban találsz egy "generátort" ami a testdb.db nevű fájlba generál tesztadatot
a config.txt-ben lehet megadni hogy hol találja az alkalmazás az adatbázist
a specifikáció alapján lehet: 
    -keresni: filterezéssel a megfelelő szövegdobozokkal a gombra kattintva
    -törölni: kijelölés majd delete gomb
    -hozzáadni: Edit/new/.. fülön keresztül, rendeléseknél/eladásoknál hasonló táblázatos megjelenítés egy szummázott net/gross texttel
    -a kék címke azt jelenti hogy a mező módosítva volt, csak akkor perzisztens ha commitoljuk, illetve ugyanígy lehet rollbackelni.

egyéb: alapból van a dátumra egy ____-__-__ ellenőrzés az adatbázisban, de a tableviewba be lehet írni fals adatot. 
ilyenkor ha commitolsz, az érvénytelen mezők nem lesznek elmentve, és lehet őket rollbackelni.

szívesen fogadok minden észrevételt, de megértem ha nem lesz időd ~1800 sort alaposan átnézni 
