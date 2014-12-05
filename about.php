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
<a href="SearchEngine.php"><img src="Logo.png" alt="SearchEngine"></a><br>

This search engine was built by me, Katherine Chen, for <a href="http://www.csce.uark.edu/~sgauch/4013-IR/F14/index.html">CSCE 4013 Information Retrieval</a> at the University of Arkansas.<br><br>
The program was written in Java with <a href="https://javacc.java.net/">JavaCC</a> used to tokenize.<br><br>
<a href="http://www.csce.uark.edu/~sgauch/4013-IR/F14/index.html">Stopwords</a> and low frequency words in the document collection 
are ignored in the indexing process since these words do not contribute much to the meaning of the document.<br><br>
The document collection from which the search engine indexed can be found <a href="http://www.csce.uark.edu/~sgauch/4013-IR/files/">here</a>.<br><br>
The source code can be found <a href="https://github.com/squigglers/SearchEngine">here</a>.

</body>
</html>