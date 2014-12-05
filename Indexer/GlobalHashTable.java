import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

public class GlobalHashTable
{
	private int size;				//size of hashtable
	private long used;				//how full the table is
	private long collisions;		//number of collisions
	private long lookups;			//number of lookups made
	private long numDocsInCollection;	//total number of documents in entire collection
	private final long LOWFREQTHRESHOLD = 2;
	private Node [] hashtable;
	
	//the datatype stored in the hashtable
	//stores the token, numDocs, and linkedlist
	protected class Node
	{
		protected String token;		//token
		protected long numDocs;		//number of documents token is in
		protected LinkedList<LinkedNode> linkedlist;	//stores the docID/freq pairs of the token
		private long freq;			//token frequency in entire collection
		
		protected Node(String token, long numDocs, long freq)
		{
			this.token = token;
			this.numDocs = numDocs;
			this.freq = freq;
			linkedlist = new LinkedList<LinkedNode>();
		}
	}
	
	//the datatype stored in the linkedlist - list of docID/freq pairs
	protected class LinkedNode
	{
		protected long docID;
		protected double frequency;
		
		protected LinkedNode(long docID, double frequency)
		{
			this.docID = docID;
			this.frequency = frequency;
		}
		
		public String toString()	//can output LinkedNode to string to show docID and frequency
		{
			return "[" + docID + ", " + frequency + "]";
		}
	}
	
	//constructor for GlobalHashTable
	//initializes hashtable arrray of size*3 with with each token = "", numDocs = 0, and empty linkedlist
	public GlobalHashTable(int size)
	{
		//initialize a few variables
		this.size = size * 3;
		used = 0;
		collisions = 0;
		lookups = 0;
		
		//create hashtable of size*3 and initialize with token = "", numDocs = 0, and empty linkedList
		hashtable = new Node[this.size];
		for(int i = 0; i < this.size; i++)
			hashtable[i] = new Node("", 0, 0);
	}
	
	//copy constructor for GlobalHashTable
	public GlobalHashTable(GlobalHashTable ht)
	{
		//copy  variables
		size = ht.size;
		used = ht.used;
		collisions = ht.collisions;
		lookups = ht.lookups;
		
		//copy hashtable
		hashtable = new Node[size];
		for(int i = 0; i < size; i++)
		{
			hashtable[i] = new Node(ht.hashtable[i].token, ht.hashtable[i].numDocs, ht.hashtable[i].freq);
			for(LinkedNode a: ht.hashtable[i].linkedlist)	//copy linkedlist
				hashtable[i].linkedlist.add(a);
		}
	}
	
	//print to file the contents of the non-null entries of the hashtable
	public void Print(String dictFilename, String postFilename)
	{
		long start = 0;	//start value in dict file that points to the post file line where the token's documents starts
		double idf, tfIdf;
		int fixedTfIdf;
		
		//formatting dict and post column lengths
		int tokenLengthFormat = 20;
		int numDocsLengthFormat = 4;
		int startLengthFormat = 6;
		String dictFormatting = "%-" + (tokenLengthFormat+1) + "s" +
								"%-" + (numDocsLengthFormat+1) + "d" +
								"%-" + (startLengthFormat) + "d";
		int docIdLength = 4;
		int fixedTfIdfLength = 7;
		String postFormatting = "%-" + (docIdLength+1) + "d" +
								"%-" + fixedTfIdfLength + "d";

		try
		{
			BufferedWriter dict = new BufferedWriter(new FileWriter(dictFilename));
			BufferedWriter post = new BufferedWriter(new FileWriter(postFilename));
			for(int i = 0; i < size; i++)
			{
				//output a line if there is an entry in the hashtable
				if(hashtable[i].freq >= LOWFREQTHRESHOLD)
				{
					//calculate idf of token
					idf = calcIdf(numDocsInCollection, hashtable[i].numDocs);
					
					//print to dict
					String shorterToken;
					if(hashtable[i].token.length() > tokenLengthFormat)
						shorterToken = hashtable[i].token.substring(0, 10) + 
							hashtable[i].token.substring(hashtable[i].token.length()-10, hashtable[i].token.length()); 
					else
						shorterToken = hashtable[i].token;
					String dictOutput = String.format(dictFormatting, shorterToken, hashtable[i].numDocs, start);
					dict.write(dictOutput + "\n");	//print dict line

					//print to post
					for(LinkedNode a: hashtable[i].linkedlist)		//print post lines
					{
						
						tfIdf = a.frequency * idf;	//calculate tf*idf
						fixedTfIdf = (int)(tfIdf * 10e5);
						
						String postOutput = String.format(postFormatting, a.docID, fixedTfIdf);
						post.write(postOutput + "\n");
						start++;
					}
				}
				
				//otherwise clear node in hasthable and output blank -1 -1 to dict if entry in hashtable is empty
				else
				{
					String blank;
					if(hashtable[i].token != "")
						blank = "removedremoved";
					else
						blank = "blankblankblank";
					
					String dictOutput = String.format(dictFormatting, blank, -1, -1);
					dict.write(dictOutput + "\n");
				}
			}
			dict.close();
			post.close();
		}
		catch(IOException e)
		{
			System.err.println("IOError: " + e.getMessage());
		}
		
		//System.out.println("Collisions: " + collisions + ", Used: " + used +  ", Lookups: " + lookups);
	}
	
	//calculates idf as 1 + log(N/numdocs) 
	//N = total number of documents in collection, numdocs = number of docs term is in
	private double calcIdf(long N, long numdocs)
	{
		return Math.log10((double)N/(double)numdocs);
	}
	
	//insert LocalHashTable into GlobalHashTable through a LinkedList
	public void Insert(long docID, LocalHashTable ht)
	{
		double normalizedTf;
		for(int i = 0; i < ht.getSize(); i++)	//go through each Node in LocalHashTable
		{
			String token = ht.getNodeToken(i);
			if(token != "")
			{
				int index = Find(token);			
				
				//if token is not already in the table, insert it with a numDocs of 1
				if(hashtable[index].token == "")
				{
					hashtable[index].token = token;
					hashtable[index].numDocs = 1;
					hashtable[index].freq = ht.getNodeFrequency(i);
					used++;
				}
				
				//if token is already in table, update numDocs by adding 1
				else
				{
					hashtable[index].numDocs++;
					hashtable[index].freq += ht.getNodeFrequency(i);
				}
				
				//calculate normalized term frequency to store in LinkedNode frequency
				normalizedTf = getNormalizedTf((double)ht.getNodeFrequency(i), (double)ht.getTotalTokens());
				
				//insert docID and normalizedTf to linkedlist
				hashtable[index].linkedlist.add(new LinkedNode(docID, normalizedTf));
			}
		}		
	}
	
	//calculate normalized term frequency as term frequency divided by total tokens in document
	public double getNormalizedTf(double termFreq, double totalTokens)
	{
		return termFreq/totalTokens;
	}
	
	//locates token in hashtable
	//returns either the index of the word in the table or
	//returns the index of the free space in which to store the word
	protected int Find(String token)
	{
		long sum = 0;
		int index;
		
		//hashfunction - add all the characters of the key together
		for(int i = 0; i < token.length(); i++)
			sum = sum * 19 + token.charAt(i);	//multiply sum by 19 and add byte value of character
		if(sum < 0)								//if calculation of sum was negative, make it positive
			sum = sum * -1;
		
		index = (int) (sum % size);

		// Check to see if word is in that location
		// If not there, do linear probing until word found or empty location found.
		while(!hashtable[index].token.equals(token) && hashtable[index].token != "") 
		{
			index = (index+1) % size;
			collisions++;
		}
		
		return index;
	}
	
	//returns all [docID, frequency] LinkedNodes at the hashed location of token
	public String getDocs(String token)
	{
		int index = Find(token);
		String docs = "";
		for(LinkedNode a: hashtable[index].linkedlist)		//get all the docID/freq pairs through all the LinkedNodes
			docs = docs + a.toString() + " ";
		docs.trim();
		
		lookups++;
		return docs;
	}
	
	//return collisions, used, and lookups as a string
	public String getUsage()
	{
		return "Collisions: " + collisions + ", Used: " + used +  ", Lookups: " + lookups;
	}
	
	//return number of collisions
	//how much you need to improve your hash function
	public long getCollisions()
	{
		return collisions;
	}
	
	//return how full the table is
	public long getUsed()
	{
		return used;
	}
	
	//return the number of lookups made
	public long getLookups()
	{
		return lookups;
	}
	
	//sets the total number of documents in entire collection
	public void setNumDocsInCollection(long N)
	{
		numDocsInCollection = N;
	}
}
