<!DOCTYPE html>
<html>

<head>
<title>SearchEngine</title>

<!-- CSS it up! -->
<style>
	body {font-family: Arial;
			margin-left: 3%;}
	a {color: #747eff}
	a:hover {color: #ff9090}
	#userQuery {width: 170px}
	.query {color: #ff9090}
	.weight {color: #ff9090}
</style>
</head>

<body>

<!-- Logo -->
<a href="SearchEngine.php"><img src="Logo.png" alt="SearchEngine"></a>

<!-- input form for the user to type the search query -->
<form method="get" action="SearchEngine.php">
<input type="text" name="query" id="userQuery">
<input type="submit" value="Search">
</form>

<?php

//folder where all the documents are located
$docFolder = 'http://www.csce.uark.edu/~sgauch/4013-IR/files/';

//retrieve query results
if(!empty($_GET)){
	$query = $_GET['query'];
	$query = trim($query);
	$results = shell_exec("java -cp Retrieve Retrieve $query");
	$resultsArray = preg_split('/\s+/', $results);
	$querySpan = "<span class=\"query\">" . $query . "</span>";
}	

//welcome message
if(empty($_GET))
	echo "<a href=\"about.php\">Hello! Would you like to learn more about this search engine?</a>";

//no results found
else if(!empty($_GET) && strlen($query) == 0)
	echo "Did you mean to leave the query empty?";
	
//no results found
else if(!empty($_GET) && strlen($results) == 0)
	echo "No results were found for $querySpan.";

//show results [title (with url), document name, and document weight
else if(!empty($_GET) && strlen($results) != 0){
	echo "Showing top 10 results for $querySpan.<br><br>";
	for($i = 0; $i < sizeof($resultsArray)-2; $i = $i + 2) {
		$docName = $resultsArray[$i];
		$docUrl = $docFolder . $docName;
		$docTitle = getTitle($docUrl);
		$docWeight = intval($resultsArray[$i+1] / 100);
		
		if(strlen($docTitle) == 0)	//just in case a site doesn't have a title
			$docTitle = "Untitled";
		echo "<a href=$docUrl>$docTitle</a><br>";
		echo "$docName <span class=\"weight\">$docWeight</span><br><br>";
	}
}
	
//function to get the title of a web page
function getTitle($url){
	$file = file_get_contents($url);
	if(strlen($file) > 0){
        preg_match("/<title>(.*)<\/title>/siU", $file, $title);
        return $title[1];
    }
}
?>

</body>
</html>