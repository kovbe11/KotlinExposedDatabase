Ez a projekt a Kotlin alapú szoftverfejlesztés tárgyra készült mint nagyházi feladat. A tanár úr külön kérte hogy ne terheljük
túl tartalmas dokumentációval, így pedig nyilván nem volt indíttatásom magamnak elkészíteni.


A tárgy oktatójának írt specifikációm:

Egy kis egyéni vállalkozónak szüksége van egy árucikkeket, boltokat eladásokat
és rendeléseket nyilvántartó alkalmazásra. Ezt a háttérben egy sqlite adatbázis
fogja nyilvántartani és egy tornadofx alkalmazással fogom a felhasználó
számára is elérhetővé tenni az Exposed nevű könyvtár segítségével.

Alapvető funkciók lesznek megvalósítva, mint:
 elemek felvétele
 elemek törlése
 elemek módosítása
 rendezés
 keresés

A tárgy oktatójának írt readme:

a DBCreator.kt-ban találsz egy "generátort" ami a testdb.db nevű fájlba generál tesztadatot
a config.txt-ben lehet megadni hogy hol találja az alkalmazás az adatbázist
a specifikáció alapján lehet: 
    -keresni: filterezéssel a megfelelő szövegdobozokkal a gombra kattintva
    -törölni: kijelölés majd delete gomb
    -hozzáadni: Edit/new/.. fülön keresztül, rendeléseknél/eladásoknál hasonló táblázatos megjelenítés egy szummázott net/gross texttel
    -a kék címke azt jelenti hogy a mező módosítva volt, csak akkor perzisztens ha commitoljuk, illetve ugyanígy lehet rollbackelni.

egyéb: alapból van a dátumra egy ____-__-__ ellenőrzés az adatbázisban, de a tableviewba be lehet írni fals adatot. 
ilyenkor ha commitolsz, az érvénytelen mezők nem lesznek elmentve, és lehet őket rollbackelni.

