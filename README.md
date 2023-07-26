# SparkStreamingProject

## Description
Ce projet est une application de streaming basée sur Kafka et Spark qui lit les données à partir d'un fichier CSV, les envoie à un sujet Kafka, les traite, les enregistre dans une base de données SQLite, puis les visualise et effectue une détection d'anomalies en utilisant un modèle de machine learning.

## Fonctionnalités

### Génération des données
- Initialisation du producteur Kafka.
- Lecture des données à partir du fichier `power_consumption_data.csv`.
- Envoi des données au sujet Kafka `first`.

### Traitement des données
- Utilisation de Spark Streaming pour consommer et traiter les messages à partir du sujet Kafka `first`.
- Les données sont ensuite sauvegardées dans une base de données SQLite pour un traitement ultérieur.

### Visualisation et détection des anomalies

#### Visualisation des données et détection d'anomalies initiale (`app.py`):
- L'application Flask `app.py` sert une interface web pour visualiser les données.
- Elle se connecte à une base de données SQLite à `/Users/annadiaw/Desktop/ProjetSparkStreaming/data.db` pour récupérer les données stockées de la table `power_data`.
- Les colonnes 'Date' et 'Time' sont converties en une seule colonne 'Datetime'.
- Après le nettoyage et la préparation des données, le modèle `IsolationForest` est utilisé pour détecter les anomalies. Les résultats de la détection peuvent être visualisés à l'aide de Plotly, permettant ainsi d'identifier visuellement les points de données qui sont considérés comme anormaux.
- Les visualisations sont générées à l'aide de Plotly, offrant des représentations graphiques interactives des données et des anomalies détectées.

#### Détection d'anomalies avancée (`app_ai.py`):
- L'application Flask `app_ai.py` utilise un modèle pré-entraîné pour détecter les anomalies dans les données de consommation d'énergie en temps réel.
- Les données sont normalisées à l'aide d'un "scaler" pré-entraîné.
- Un tampon (`buffer`) est utilisé pour stocker les dernières `n` entrées de données, et une liste d'erreurs (`errors`) est utilisée pour stocker les dernières `m` erreurs pour la détection d'anomalies.
- Lorsque de nouvelles données sont reçues, elles sont ajoutées au tampon, le modèle de détection d'anomalies est appliqué, et les anomalies potentielles sont identifiées.
- Ces anomalies sont ensuite présentées à l'utilisateur via l'interface web.

## Configuration
### Pré-requis :
- Apache Kafka
- Apache Spark
- SQLite
- Flask
- Python 3.x
- Bibliothèques Python : pandas, numpy, plotly, scikit-learn, seaborn

### Exécution du projet :
Pour exécuter ce projet, il faut télécharger les fichiers **data.db** et **power_consumption_data** en accédant au au [lien suivant](https://drive.google.com/drive/folders/1ajVoVYxxZ9_fY3GojbA_y9z9jx42aS01?usp=sharing) et les placer respectivement à la racine du projet, et dans /power-consumption-spark-streaming-app/resources/.
1. Démarrer le serveur Zookeeper et le serveur Kafka.
2. Exécuter `DataGenerator.scala` pour générer et envoyer les données au sujet Kafka `first`.
3. Exécuter `DataProcessor.scala` pour consommer et traiter les messages du sujet Kafka `first`, et pour sauvegarder les données dans une base de données SQLite.
4. Exécuter `app.py` pour servir une page web qui affiche les visualisations des données.
5. Exécuter `app_ai.py` pour effectuer une détection d'anomalies sur les données reçues en utilisant un modèle de machine learning pré-entraîné.

### Commandes utiles :
Les commandes pour interagir avec SQLite et Kafka sont fournies dans le fichier `commandsv2.txt`.

## Contact
Pour toute question ou suggestion, n'hésitez pas à me contacter à <annadiaw2000@gmail.com>.
