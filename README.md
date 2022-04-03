# Tamagoshi
Projet de jeu de tamagoshis en Java et JavaFX par Alexandre BOUSQUET, étudiant en LP APIDAE

## Documentations :
- [Public](https://webinfo.iutmontp.univ-montp2.fr/~bousqueta/)
- [Protected]()
- [Private]()

## Exécutables :
- Exécutables Windows et Linux disponible à cette adresse : [Lien](https://drive.google.com/drive/folders/1cwEwwberTpGBEMmCzUfcOwWwcdDTiJWO?usp=sharing)

## Fonctionnalités et utilisation des connaissances :
- Diverses collections utilisées :
  - ArrayList pour les tamagoshis
  - Iterator avec le removeIf() dans la méthode TamaGameGraphique.nextCycle()
  - Hashmap pour les langues du jeu
- Modularisation du projet
- i18n avec 2 langues (Français et Anglais)
- Exceptions personnalisées NegativeLifeTimeException et TamagoshiNumberException pour la version CLI du jeu
- Logger utilisé (avec modification du root logger) pour la version CLI du jeu
- Persistance des données (paramètres et scores) avec fichier Properties
- Utilisation des design pattern Factory et Singleton pour la fabrique de tamagoshis afin de respecter le principe 
Open Close de SOLID pour la création de futur tamagoshis sans modifier le code de l'application
- Utilisation du design pattern Composite pour l'interface graphique (en JavaFX tout est Node donc une Node peut contenir une autre Node)
- Javadoc écrite et sites générés par visibilité
- Utilisation du polymorphisme avec la classe Tamagoshi
- Les mêmes classes permettent de lancer le jeu en version CLI et en version interface graphique (pas de duplication de méthodes)
- Utilisation de ressources comme des images ou fichier CSS
- Gestion des scores en fonction de la difficulté et de la durée de vie des tamagoshis

## Informations complémentaires : 
Je n'ai pas réussi à créer un exécutable portable avec Jlink malgré mes nombreuses heures d'essais. Je voulais que le Jlink se fasse avec le Jar que j'ai réalisé, mais impossible d'arriver à mes fins.
En théorie j'aurais pu y arriver avec la commande :
```shell
jlink --module-path C:\Users\darkb\Desktop\JDK\javafx-sdk-17.0.2\lib  --add-modules fr.iut.bousqueta  --output release --launcher prog=fr.iut.bousqueta\graphic\tamagoshi.MainGraphic
```
Cependant, j'ai des problèmes avec le path de Jlink (pas mappé nativement sur Windows 10) et avec la détection de mon module. Cela aurait peut-être été plus simple en utilisant Maven.