# Regression-using-Gradient-Descent (Java)
A Machine Learning research on Regression using Gradient Descent.

___________________________________________________________________________________________________________________________________________________________

## Datasets:
1. Heart Failure Clinical Records Dataset.xlsx --> https://archive.ics.uci.edu/ml/datasets/Heart+failure+clinical+records (UC Irvine Machine Learning Repository)
2. Melbourne Rain Prediction 1-7-08 to 30-11-08.xlsx & Perth Rain Prediction 1-7-08 to 30-11-08.xlsx --> https://www.kaggle.com/datasets/jsphyg/weather-dataset-rattle-package (Kaggle)

## Brute Force vs Ternary Search
#### usedCarsOne.java (Brute force) / usedCarsOneTernarySearch.java (Ternary Search)
- Given a dataset of 1 independent variable and 1 dependent variable, train a model to provide 1 threshold value for the independent variable such that it maximizes the accuracy of the prediction.
#### usedCarsTwo.java (Brute force) / usedCarsTwoTernarySearch.java (Ternary Search)
- Given a dataset of 2 independent variables and 1 dependent variable, train a model to provide 2 threshold values for each of the 2 independent variables such that it maximizes the accuracy of the prediction.

## Regression
- Both LinearRegression.java and LogisticRegression.java allows multiple independent variables

___________________________________________________________________________________________________________________________________________________________

## FAQs
- How to add JFreeChart library in IntelliJ?
1. Select File > Project Structure > Project Structure > Project Settings > Libraries
2. Click + then select "From Maven"
3. From the "Download Library from Maven Repository" search box, enter org.jfree:jfreechart:1.5.0 
4. Press the magnifying glass to search
6. Press OK

- How to access downloaded csv file from Mac?
Example: getDataFromCSV("/Users/.../Downloads/data.csv");

Note: Remove the first row of labels from the csv files
