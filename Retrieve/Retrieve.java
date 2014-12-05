/* Generated By:JavaCC: Do not edit this line. Retrieve.java */
import java.io.*;
import java.util.*;
import java.nio.channels.FileChannel;

public class Retrieve implements RetrieveConstants {
        //constant variables relating to the dict, post, and map files
        final static String DICTFILENAME = "dict.txt";
        final static String POSTFILENAME = "post.txt";
        final static String MAPFILENAME = "map.txt";
        final static long NUMDOCS = 1683;
        final static long DICTRECORDSNUM = 43810 * 3;
        final static long DICTRECORDSIZE = 33;
        final static long POSTRECORDSIZE = 13;
        final static long MAPRECORDSIZE = 12;
        final static String BLANKTOKEN = "blankblankblank";
        final static int NUMRESULTS = 10;

        public static void main(String[] args) throws ParseException, FileNotFoundException, IOException
        {
                //change command line arguments into a single string query
                StringBuilder querySB = new StringBuilder();
                for(String s: args)
                        querySB.append(s + " ");
                String query = querySB.toString().trim();

                //parse the query and store tokens in arraylist tokens
                ArrayList<String> tokens = new ArrayList<String>();
                java.io.StringReader sr = new java.io.StringReader(query);
                java.io.Reader r = new java.io.BufferedReader(sr);
                Retrieve parser = new Retrieve(r);
                parser.Start(tokens);

                //make accumulator to store accumulated weights and initialize to 0
                ArrayList<Weight> acc = new ArrayList<Weight>((int)NUMDOCS);
                for(int i = 0; i < NUMDOCS; i++)
                        acc.add(new Weight(i, (long)0));

                //process dict and post files
                processDict(tokens, acc);

                //sort the accumulator by decreasing weight
                Collections.sort(acc, Collections.reverseOrder());

                //present results to the user
                printResults(acc);
        }

        //print out the top results
        public static void printResults(ArrayList<Weight> acc) throws IOException, FileNotFoundException
        {
                //open map file
                RandomAccessFile mapFile = new RandomAccessFile(MAPFILENAME, "r");

                //print results
                boolean hasResults = false;
                for(int i = 0; i < NUMRESULTS && acc.get(i).weight != 0; i++)
                {
                        //find and seek to location in map file
                        long loc = acc.get(i).docid * MAPRECORDSIZE;
                        mapFile.seek(loc);

                        //print filename and weight to user
                        System.out.print(mapFile.readLine() + " " + acc.get(i).weight + " ");

                        hasResults = true;
                }
/*
		//print "NO RESULTS" if there are no results for the query
	  	if(!hasResults)
	  		System.out.print("NO RESULTS");
*/
        }

        //process dict file to find the postings
        public static void processDict(ArrayList<String> tokens, ArrayList<Weight> acc) throws IOException, FileNotFoundException
        {
                //open dict file
                RandomAccessFile dictFile = new RandomAccessFile(DICTFILENAME, "r");

                //look up tokens in dict file
                for(String t: tokens)
                {
                        //shorten token processing
                        String token = shortenToken(t);

                        //find and seek to location in dict file
                        long loc = hashfunction(token, DICTRECORDSNUM) * DICTRECORDSIZE;
                        dictFile.seek(loc);

                        //linear probe through hashtable until the token or blanktoken is encountered
                        String dictToken;
                        long dictNumDocs, dictStart;
                        do
                        {
                                //read line and get the token, numdocs, and start
                                String dictLine = dictFile.readLine();
                                String[] splitDictLine = dictLine.split("\u005c\u005cs+");
                                dictToken = splitDictLine[0];
                                dictNumDocs = Long.parseLong(splitDictLine[1]);
                                dictStart = Long.parseLong(splitDictLine[2]);

                                //if end of file go back to beginning of file
                                if(dictFile.getFilePointer() >= dictFile.length())
                                        dictFile.seek(0);
                        } while(!dictToken.equals(token) && !dictToken.equals(BLANKTOKEN));

                        //process postings and add term weights to accumulator
                        if(dictToken.equals(token))
                                processPostings(dictNumDocs, dictStart, acc);
                }

                //close dict file
                dictFile.close();
        }

        //processes the postings and adds term weights to the accumulator
        public static void processPostings(long numDocs, long start, ArrayList<Weight> acc) throws IOException, FileNotFoundException
        {
                //open post file
                RandomAccessFile postFile = new RandomAccessFile(POSTFILENAME, "r");

                //find and seek to location in post file
                long loc = start * POSTRECORDSIZE;
                postFile.seek(loc);

                //add weights to accumulator
                for(int i = 0; i < numDocs; i++)
                {
                        //read line and get the docid and weight
                        String postLine = postFile.readLine();
                        String[] splitPostLine = postLine.split("\u005c\u005cs+");
                        int docId = Integer.parseInt(splitPostLine[0]);
                        long weight = Long.parseLong(splitPostLine[1]);

                        //add weight to acc[docid]
                        long newWeight = weight + acc.get(docId).weight;
                        acc.get(docId).weight = newWeight;
                }

                //close post file
                postFile.close();
        }

        //shortens the token to length 20 if greater than length 20
        public static String shortenToken(String token)
        {
                long maxTokenLength = 20;
                String shorterToken;

                if(token.length() > maxTokenLength)
                        shorterToken = token.substring(0, 10) +
                                token.substring(token.length()-10, token.length());
                else
                        shorterToken = token;

                return shorterToken;
        }

        //calculates hashfunction on the token and returns index location
        public static long hashfunction(String token, long numDictRecords)
        {
                long sum = 0;
                int index;

                //hashfunction - add all the characters of the key together
                for(int i = 0; i < token.length(); i++)
                        sum = sum * 19 + token.charAt(i);       //multiply sum by 19 and add byte value of character
                if(sum < 0)                                                             //if calculation of sum was negative, make it positive
                        sum = sum * -1;

                index = (int) (sum % numDictRecords);

                return index;
        }

        //Weight class to store the docid and the weight of the accumulator
        public static class Weight implements Comparable<Weight>
        {
                public int docid;
                public long weight;

                public Weight(int docid, long weight)
                {
                        this.docid = docid;
                        this.weight = weight;
                }

                @Override
                public int compareTo(Weight other) {
                        if(this.weight < other.weight)
                                return -1;
                        else if(this.weight == other.weight)
                                return 0;
                        else
                                return 1;
                }
        }

//read tokens and adds them to arraylist tokens
  final public void Start(ArrayList<String> tokens) throws ParseException {
        Token t;
        String a;
    label_1:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case PHONE_NUM:
      case URL:
      case NUM_SLASH:
      case TEXT:
      case FORMATTED_TEXT:
        ;
        break;
      default:
        jj_la1[0] = jj_gen;
        break label_1;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case PHONE_NUM:
      case NUM_SLASH:
      case TEXT:
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case TEXT:
          t = jj_consume_token(TEXT);
          break;
        case PHONE_NUM:
          t = jj_consume_token(PHONE_NUM);
          break;
        case NUM_SLASH:
          t = jj_consume_token(NUM_SLASH);
          break;
        default:
          jj_la1[1] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
        }
                                a = t.image.toLowerCase();
                                tokens.add(a);
        break;
      case FORMATTED_TEXT:
        t = jj_consume_token(FORMATTED_TEXT);
                                a = t.image.toLowerCase();
                                a = a.replaceAll("<.*?>", "");
                                tokens.add(a);
        break;
      case URL:
        t = jj_consume_token(URL);
                                a = t.image;
                                tokens.add(a);
        break;
      default:
        jj_la1[2] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
    jj_consume_token(0);
  }

  /** Generated Token Manager. */
  public RetrieveTokenManager token_source;
  SimpleCharStream jj_input_stream;
  /** Current token. */
  public Token token;
  /** Next token. */
  public Token jj_nt;
  private int jj_ntk;
  private int jj_gen;
  final private int[] jj_la1 = new int[3];
  static private int[] jj_la1_0;
  static {
      jj_la1_init_0();
   }
   private static void jj_la1_init_0() {
      jj_la1_0 = new int[] {0x11e00000,0x1a00000,0x11e00000,};
   }

  /** Constructor with InputStream. */
  public Retrieve(java.io.InputStream stream) {
     this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public Retrieve(java.io.InputStream stream, String encoding) {
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new RetrieveTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 3; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream) {
     ReInit(stream, null);
  }
  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 3; i++) jj_la1[i] = -1;
  }

  /** Constructor. */
  public Retrieve(java.io.Reader stream) {
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new RetrieveTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 3; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 3; i++) jj_la1[i] = -1;
  }

  /** Constructor with generated Token Manager. */
  public Retrieve(RetrieveTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 3; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(RetrieveTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 3; i++) jj_la1[i] = -1;
  }

  private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }


/** Get the next Token. */
  final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

/** Get the specific Token. */
  final public Token getToken(int index) {
    Token t = token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  private java.util.List<int[]> jj_expentries = new java.util.ArrayList<int[]>();
  private int[] jj_expentry;
  private int jj_kind = -1;

  /** Generate ParseException. */
  public ParseException generateParseException() {
    jj_expentries.clear();
    boolean[] la1tokens = new boolean[30];
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 3; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 30; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.add(jj_expentry);
      }
    }
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = jj_expentries.get(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  /** Enable tracing. */
  final public void enable_tracing() {
  }

  /** Disable tracing. */
  final public void disable_tracing() {
  }

}
