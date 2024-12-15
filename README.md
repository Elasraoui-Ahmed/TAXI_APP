# 🚖 Compteur de Taxi  

![compteurtaxi](https://github.com/user-attachments/assets/6f267d01-dd60-41e6-98c2-e4f221212357)

## Description  
Le projet **Compteur de Taxi** est une application Android développée en Kotlin, simulant le fonctionnement d’un compteur de taxi en temps réel. Il offre une expérience utilisateur fluide et pratique grâce à :  
- Le suivi en temps réel de la localisation via Google Maps.  
- Le calcul dynamique des tarifs basés sur la distance parcourue et le temps écoulé.  
- L’affichage du profil détaillé du chauffeur avec un QR Code.  
- Des notifications pour informer de la fin de la course et afficher le tarif total.  
- Une interface multilingue (Arabe, Français, Anglais) et un mode sombre.  

## Fonctionnalités Clés  
### 🚗 **Suivi en Temps Réel**  
- Affichage de la position du chauffeur sur une carte Google Maps.  
- Mise à jour dynamique de la distance, du temps et du tarif de la course.  

### 🧑‍✈️ **Affichage du Profil**  
- Informations complètes sur le chauffeur (nom, âge, type de permis).  
- Génération et affichage d’un QR Code pour partager ses informations.  

### 💰 **Calcul des Tarifs**  
- Tarif de base : **2.5 DH**.  
- Tarif par kilomètre : **1.5 DH/km**.  
- Tarif par minute : **0.5 DH/min**.  
- Calcul dynamique et mise à jour en fonction de la localisation.  

### 🔔 **Notifications**  
- Notification automatique à la fin de la course avec récapitulatif du trajet.  

### 🌐 **Multilingue et Mode Sombre**  
- Support de 3 langues : Arabe, Français et Anglais.  
- Mode sombre pour une utilisation agréable dans les environnements peu lumineux.  

## Technologies Utilisées  
- **Kotlin** : Langage principal de développement.  
- **Google Maps API** : Intégration des cartes et localisation en temps réel.  
- **EasyPermissions** : Simplification de la gestion des permissions Android.  
- **Android Jetpack** : Navigation, LiveData, ViewModel.  

## Prérequis  
- Android Studio installé.  
- Clé API Google Maps configurée.  
- Appareil ou émulateur Android avec localisation activée.  

