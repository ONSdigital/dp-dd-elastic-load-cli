# Elastic Search Commandline Tool
This a a commandline tool to load the data into the ElasticSearch instance.


## Commandline Options
There are various formats of document builders;

* DatasetHeaders - 
Creates a ElasticSearch document from the metadata (the data.json file) and the headers of the csv file (data.csv)
* IndividualDataSet - Splits each DataSet row in the data.csv file into an individual Json file
* JsonDataSet - Loads the data.json file as teh metadata, and adds the headers found in data.csv file to the metadata section. Then builds a collections from the data.csv file into individual fields for each column (i.e. "time":"20:00")  
* RowbasedDataSet - Adds the metadata from data.json and the converts the rows from data.csv files into a a single 'json String' field for the whole row. (I.E. this allows Elastic to search the heading titles and the heading rows simultaneously and only uses a single field definition for the whole row - to limit the issue where we hit the 1,000 field limtiation)  

To run the convertions on a single file you need to run on of the indiviudal actions.. convertCSVTo... and supply a directory that has both csv and json data files.

`"convertCSVToStringRows -c "/Users/fawks/work/data/exampleData/CPI15 Consumer Prices Index" -f ./dataString.json`


To bulk load a set of these to an ElasticSearch service you need to use the bulk loading feature

`convertAllCsvFiles -c "/Users/fawks/work/data/exampleData" -s ROWS`

where the structure `-s` flag is one of the ENUMS `HEADERS, ROWS, JSON, INDIVIDUAL`
and the content `-c` directory can be the root directory for many csv/json files. 
 

## Data structure
It is expected that each directory will have one `data.json` file that contains metadata in the json structure (any json fields will do) and along side the json file will be a *Standard* csv file where the first row is the header names and subsequent rows are the data elements