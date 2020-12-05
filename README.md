# DERI2019
An Analysis of Twitter Users Political Views using Cross-Account Data Mining

## Table of contents
* [Rationale](https://github.com/ShivramR/DERI-2019/edit/main/README#Rationale)
* [Accomplishments](https://github.com/ShivramR/DERI-2019/edit/main/README#Accomplishments)
* [Future work](https://github.com/ShivramR/DERI-2019/edit/main/README#Future-work)
* [Repository files](https://github.com/ShivramR/DERI-2019/edit/main/README#Repository-files)

## Rationale
Prior to the 2016 presidential election, polling largely failed to predict the upset and victory of Donald Trump. In the United States, more than 70% of the population are social media users and as one of the most popular social network websites, Twitter has exceeded a hundred million daily users and nearly one billion total users. This diverse range on one universal platform leads to the representation of almost any political view. Therefore, as a more accurate predictor for the upcoming election year and possible future elections, it could be beneficial to look at the change in the opinions of users across a relatively long period of time. In this paper, we conducted a study on the political view migration through following up a selected group of Twitter users over the period of three years.

## Accomplishments
* Analyzed political trends of a random sample of 60,491 US-based Twitter users
* Developed and deployed a seed-based algorithm for classfifying political shifts
* Formed a testable network-based classification model
* Reached and manually verified a conclusion of more moderate positions in a cyclical manner
* Paper published in international SMART-DSC2019 Conference
  * [Springer link](https://doi.org/10.1007/978-981-15-2407-3_16)

## Future work
* Expanding the sample size
* Explore political deviations between Twitter and other platforms such as Facebook and Instagram
* Verify the classification algorithm with other datasets
* Potentially introduce natural language processing for greater classification accuracy

## Repository files
* [Presentation slide](https://github.com/ShivramR/DERI-2019/Presentation_Slide.pdf)
* [Research paper](https://github.com/ShivramR/DERI-2019/Research_Paper.pdf)
* [Iterative classifier](https://github.com/ShivramR/DERI-2019/IterativeClassifier.java)
  * A Java script that implements the political seed-based classification algorithm described in [here](https://github.com/ShivramR/DERI-2019/Research_Paper.pdf)
* [Loop runner](https://github.com/ShivramR/DERI-2019/Loop_run.java)
  * Another Java script used for multiple runs of the algorithm to measure deviance and verify precision
* [Common users finder](https://github.com/ShivramR/DERI-2019/common.py)
  * A Python script that uses Pandas and a config.txt file to find common users in all three years of 2017-2019 of the given user set
