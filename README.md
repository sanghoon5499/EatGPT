# EatGPT

Just input your cravings and EatGPT will return you some nearby restaurants that best fits that description! <br /><br />

![image](https://github.com/sanghoon5499/EatGPT/assets/17420160/731b5ee7-1488-4377-a294-0ef1f0995cf7)

Here's a quick example, "something cheesy but not deep fried"<br />
You get a few nearby restaurants that fit that description:<br />
![image](https://github.com/sanghoon5499/EatGPT/assets/17420160/ac9d17f7-5ed7-4f02-b1fa-783acedb4545)
<br /><br />
You can also check out where it is on Google Maps!<br />
![image](https://github.com/sanghoon5499/EatGPT/assets/17420160/948b5c86-6da9-4695-9b76-e4a8264875e1)
<br /><br />

# Tech used
- Google Places API
- ChatGPT API
- Hopefully one day: ChromaDB (Vector Database)
<br /><br />

# Pipeline Flow
1. Users use chat window to “text” the application asking which restaurants to eat at 
2. ChatGPT decode the specific terms and determine which cuisine might fit
3. Search terms into google maps api and get JSON results
4. Store JSON into ChromaDB (Vector Space Database)
   - Utilize user query in search bar as well as GPT’s cuisine detection
   - I don’t know what ChromaDB will return, but assuming it will return a list of objects as results of the query
5. Application selects top 3 results
6. Based on the cuisine, ratings, etc of the top 3, ChatGPT API creates a short description for each restaurant and displays them to the use
7. For each restaurant and description, create a small tile to display the result in and populate the data that way
8. Each tile will be clickable and leads to the map, which displays the restaurant’s location
   - Have a button that exports the user out of the app into Google Maps with the restaurant data so that they can navigate to it  
<br /><br />

# Things to Consider / Features yet to be added
 - note: code made public without api keys anywhere; need to create gitignore and set things up later
 - Need to clear the database at one point (30 day limit?)
 - Should probably let the user choose the cuisine (top three selections by ChatGPT)
 - Add in feature to send user to actual google maps for directions

<br /><br />

# Personal learning + <br />
# Technology Overview 1: Vector Databases
This is my first time working with a vector database, so hopefully my explanations are correct, and more importantly, that they make sense to you. Here we go:

<b>What’s a vector?</b><br />
A numerical array of Doubles. Used to represent more complex ideas such as words and sentences. These vectors are stored in a higher-dimensional space called an “embedding”.<br />
For how a word is converted to a vector, check this article out: https://www.baeldung.com/cs/convert-word-to-vector <br />

<b>What's an embedding?</b><br />
The below image is one such example of an embedding, and each italicized word is a vector:<br /> Image from: OpenSource Connections <br />
![image](https://opensourceconnections.com/wp-content/uploads/2022/10/vector.png)<br /><br />

Simply put, in a given space, words that share similar "meaning" are closer together, and words that aren't are distanced away.<br />
There are many words that might have overlapping meanings, such as "fishing rod", which will lay between "Food" and "Tools"<br />

<b>What's a Vector Database?</b><br />
The primary purpose of the vector database is to query a search string and return results based on how close the database's contents' meanings are.
