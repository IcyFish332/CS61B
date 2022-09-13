# Gitlet Design Document

**Name**: Siyuan Lu

## Classes and Data Structures

#### Main

This is the entry point to our program. It takes in arguments from the command line and based on the command 
(the first element of the args array) calls the corresponding command in Repository which will actually execute the logic of the command. 
It also validates the arguments based on the command to ensure that enough arguments were passed in.
Besides, it will determine if this CWD has been initialed. 

#### Fields
This class has no fields and hence no associated state: 
it simply validates arguments and defers the execution to the Repository class.

### Repository

This is where the main logic of our program will live. This file will handle all the actual gitlet commands
by reading/writing from/to the correct file, setting up persistence, and additional error checking.

It will also be responsible for setting up all persistence within .gitlet. This includes creating the .gitlet folder
as well as the folder and file where we store all objects.

This class defers all specific logic to the specific class: for example, instead of having the Repository class handle
Blob serialization and deserialization, we have the Blob class contain the logic for that.

#### Fields

1. `public static final File CWD = new File(System.getProperty("user.dir"))` - The Current Working Directory. 
Since it has the package-private access modifier (i.e. no access modifier), other classes in the package may use this field. 
It is useful for the other File objects we need to use.
2. `public static final File GITLET_DIR = join(CWD, ".gitlet")` - The hidden .gitlet directory. 
This is where all of the state of the CapersRepository will be stored, including additional things like the Dog objects 
and the current story. It is also package private as other classes will use it to store their state.
3. `public static final File STAGE = join(".gitlet", "stage")` - The stage directory.
4. `public static final File BLOBS_DIR = join(".gitlet", "blobs")` - The blobs directory.
5. `public static final File COMMITS_DIR = join(".gitlet", "commits")` - The commits directory.
6. `public static final File HEAD = join(".gitlet", "HEAD")` - Points to the current commit.

### Blob

This class represents a blob that will be stored in a file. For telling the difference of all the commits,
we use SHA-1 hash code to tranverse all the commits into unique ids, which is also their filename in the "blobs" folder.

All Dog objects are serialized within the BLOBS_DIR which is within the GITLET_DIR.
The Blob class has helpful methods that will return the Blob object corresponding to some String name given to it,
as well as write that Blob to a file to persist its changes.

#### Fields

1. `private byte[] contents` - The contents of this Blob which is stored as bytes, a.k.a. the "bytes version" of this Commit, easy to write and use.
2. `private String id` - The unique id of each Blob. Ensure do not miss any new file, at the same time, do not add some file twice or more.
3. `private String filename` - The filename of the Blob. It's highly possible that many blobs holds the same filename.
   But this is only for quick search, we will need id to tell the difference eventually.

### Commit

This class represents a commit that will be stored in a file.
For telling the difference of all the commits, we use SHA-1 hash code to tranverse all the commits into
unique UIDs, which is also their filename in the "commits" folder.

All Commit objects are serialized within the COMMITS_DIR which is within the GITLET_DIR.
The Commit class has constructor for creating ture "commit" operation. Also, it has other 
helper method to write each Commit to a file to persist its changes.

#### Fields

1. `private String message` - The message of this Commit, which is provided by the`args`
2. `private Date timestamp` - The commit time of this Commit, Keeps track of the time of this Commit, returns time and date.
3. `private String UID` - The unique UID for each Commit. Implicates which the commit is through SHA-1 hash.
4. `private List<String> parents` - Last Commit of this. Every commit should record last commit, except Commit 0.
5. `private HashMap<String, String> blobs` - All the blobs this commit includes.

### Stage

This class represent the stage that will be written in a file.
It can record files after "add" and "rm" operations temporarily through two data structure.
All these files would disappear after "commit" operation. 

#### Fields

1. `private HashMap<String, String> added` - Stores files added, which will be stored as a new blob.
2. `private HashSet<String> removed` Stores files removed. These files just don't exist in the next commit, the blobs still exist.

### Utils

This class contains helpful utility methods to read/write objects or String contents from/to files, 
as well as reporting errors when they occur.

This is a staff-provided and PNH written class, so we leave the actual implementation as magic 
and simply read the helpful javadoc comments above each method to give us an idea of whether or not it’ll be useful for us.

#### Fields

Only some private fields to aid in the magic.

### MyUtils

This class has the same functionality with `Utils`. It includes helpful methods beside these in `Utils`.

#### Fields

Just as `Utils`

## Algorithms

## Persistence

