# TranspiTrack
Projet Dev M1 MIAGE

## Contributeurs
- Alexandre Crespin
- Guilherme Sampaio
- Sacha Brezisky
- Walaedine Sekoub

## SonarQube
https://sonarcloud.io/summary/overall?id=kunail0804_TranspiTrack&branch=main

## Interprétations du sujet
<img width="1174" height="180" alt="image" src="https://github.com/user-attachments/assets/ec569bfa-9d97-4552-b3fc-d55601757521" />
Nous avons décidé de considérer les activités comme des éléments que les utilisateurs saisissent après les avoir réalisées.

Nous allons sauvegarder les données météo du jour au moment de la création de l'activité pour pouvoir afficher les conditions météo le jour où l'activité a été effectuée. De plus, nous proposons un widget météo (7 jours) dans le dashboard.

Au lieu de choisir un type de sport lors de la création d'une activité, nous avons décidé de faire choisir directement un sport à l'utilisateur. Dans l'activité, le type de sport sera récupéré à partir du sport choisi.

On calcule les calories à l'aide du MET qui est indiqué par les admins.

Les activités que les utilisateurs rentrent sont visibles dans le profil.html ou sur le dashboard (onglet "activities"). Le dashboard (onglet principal) se concentre sur les indicateurs.

Le score qui est indiqué par les participants à un challenge dépend du contexte. Si le challenge concerne un nombre de pompes, alors le score est le nombre de pompes faites par l'utilisateur ; cela peut aussi être un temps, une distance...

Le classement des challenges se fait en fonction du score.


