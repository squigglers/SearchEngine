import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class LocalHashTable
{
	private int size;				//size of hashtable
	private long used;				//how full the table is
	private long collisions;		//number of collisions
	private long lookups;			//number of lookups made
	private long totalTokens;		//total number of tokens (non-unique)
	private Node [] hashtable;
		
	//the datatype stored in the hashtable
	protected class Node
	{
		protected String token;			//key
		protected long frequency;		//data
		
		protected Node(String token, long frequency)
		{
			this.token = token;
			this.frequency = frequency;
		}
	}
	
	//constructor for LocalHashTable
	//initializes hashtable array of size*3 with each token = "" and frequency = 0
	public LocalHashTable(int size)
	{
		//initialize a few variables
		this.size = size * 3;
		used = 0;
		collisions = 0;
		lookups = 0;
		totalTokens = 0;
		
		//create hashtable of size*3 and initialize with token = "" and frequency = 0
		hashtable = new Node[this.size];
		for(int i = 0; i < this.size; i++)
			hashtable[i] = new Node("", 0);
	}
	
	//copy constructor for LocalHashTable
	public LocalHashTable(LocalHashTable ht)
	{
		//copy variables
		size = ht.size;
		used = ht.used;
		collisions = ht.collisions;
		lookups = ht.lookups;
		totalTokens = 0;
		
		//copy hashtable
		hashtable = new Node[size];
		for(int i = 0; i < size; i++)
			hashtable[i] = new Node(ht.hashtable[i].token, ht.hashtable[i].frequency);
	}
	
	//print to file the contents of the non-null entries of the hashtable
	public void Print(String filename)
	{
		try
		{
			BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
			for(int i = 0; i < size; i++)
			{
				if(hashtable[i].token != "")
					bw.write(hashtable[i].token + " " + hashtable[i].frequency + "\n");
			}
			bw.close();
		}
		catch(IOException e)
		{
			System.err.println("IOError: " + e.getMessage());
		}
		
		System.out.println("Collisions: " + collisions + ", Used: " + used +  ", Lookups: " + lookups);
	}
	
	//insert token with frequency of 1 or update token with an additional frequency
	public void Insert(String token)
	{
		int index = Find(token);
		
		//if not already in the table, insert it with frequency of 1
		 if(hashtable[index].token == "")
		 {
		    hashtable[index].token = token;
		    hashtable[index].frequency = 1;
		    used++;
		 }
		 
		 //if already in table, update term frequency by adding 1
		 else
			 hashtable[index].frequency++;
		 
		 //add one to totalTokens
		 totalTokens++;
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
	
	//returns the frequency at the hashed location of token
	public long getFreq(String token)
	{
		int index = Find(token);
		lookups++;
		return hashtable[index].frequency;
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
	
	//return the size of the hashtable
	public int getSize()
	{
		return size;
	}
	
	//returns the token at location index of the hashtable
	public String getNodeToken(int index)
	{
		return hashtable[index].token;
	}
	
	//returns the frequency of the token at location index of the hashtable
	public long getNodeFrequency(int index)
	{
		return hashtable[index].frequency;
	}
	
	//returns totalTokens of the hashtable
	public long getTotalTokens()
	{
		return totalTokens;
	}
}
