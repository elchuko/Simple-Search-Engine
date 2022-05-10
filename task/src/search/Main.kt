package search

import java.io.File

fun main(args: Array<String>) {
    val _DATA = "--data"

    val argumentMap = processArguments(args)
    val fileName = argumentMap.get(_DATA)

    var file = File(fileName).inputStream()
    val arrayOfPeople = mutableListOf<String>()

    file.bufferedReader().forEachLine { arrayOfPeople.add(it) }

    val mapOfPeople = createInvertedIndex(arrayOfPeople)
    println(mapOfPeople)
    
    var option = 5
    
    while(true) {
        println("\n=== Menu ===")
        println("1. Find a person\n2. Print all people\n0. Exit")
        option = readLine()!!.toInt()
        when(option) {
            0 -> break
            2 -> printAllPeople(arrayOfPeople)
            1 -> findPeople(arrayOfPeople, mapOfPeople)
            else -> println("\nIncorrect option! Try again.")
        }
    }
    
    println("\nBye!")
}

fun createInvertedIndex(arrayOfPeople: MutableList<String>): Map<String, MutableList<Int>> {
    var map = emptyMap<String, MutableList<Int>>().toMutableMap()
    arrayOfPeople.forEachIndexed {
        index, line ->
        var split = line.split(" ")
        split.forEach {
            if (map.containsKey(it.lowercase())) {
                map[it.lowercase()]?.add(index)
            } else {
                map[it.lowercase()] = mutableListOf(index)
            }
        }
    }
    return map
}

fun processArguments(args: Array<String>): HashMap<String, String> {
    var map = HashMap<String, String>()
    for (i in 0 until args.size - 1 step 2) {
        map[args[i]] = args[i + 1]
    }
    return map
}

fun findPeople(arrayOfPeople: MutableList<String>, mapOfPeople: Map<String, MutableList<Int>>) {
    var searchStrategy = determineStrategy()

    println("\nEnter a name or email to search all matching people.")
    var query = readLine()!!.lowercase()
    var search = searchStrategy(arrayOfPeople, mapOfPeople, query)
    if (search == "") {
        println("No matching people found.")
    } else {
        println(search)
    }
}

fun determineStrategy(): (MutableList<String>, Map<String, MutableList<Int>>, String) -> String {
    println("Select a matching strategy: ALL, ANY, NONE")
    var strategy = readLine()!!
    return when (strategy) {
        "ALL" -> ::searchAllWords
        "ANY" -> ::searchAnyWord
        else -> ::searchNone
    }
}

fun searchAllWords(arrayOfPeople: MutableList<String>, mapOfPeole: Map<String, MutableList<Int>>, query: String): String {
    var results = (0..arrayOfPeople.size - 1).toMutableList()
    var split = query.split(" ")
    var trueSet: Set<Int> = setOf()
    split.forEach {
        trueSet = results.intersect(containsKey(mapOfPeole, it).toSet())
        results = trueSet.toMutableList()
    }
    return createString(arrayOfPeople, trueSet.toMutableList())
}

fun searchAnyWord(arrayOfPeople: MutableList<String>, mapOfPeole: Map<String, MutableList<Int>>, query: String): String {
    var results = mutableListOf<Int>()
    var split = query.split(" ")
    split.forEach {
        results.addAll(containsKey(mapOfPeole, it))
    }
    return createString(arrayOfPeople, results)
}

fun searchNone(arrayOfPeople: MutableList<String>, mapOfPeole: Map<String, MutableList<Int>>, query: String): String {
    var results = (0..arrayOfPeople.size - 1).toMutableList()
    var split = query.split(" ")
    split.forEach {
        results.removeAll(containsKey(mapOfPeole, it))
    }
    return createString(arrayOfPeople, results)
}


fun containsKey(mapOfPeole: Map<String, MutableList<Int>>, query: String): MutableList<Int> {
    var queryResult = mapOfPeole[query]

    return queryResult ?: mutableListOf()
}

fun createString(arrayOfPeople: MutableList<String>, queryResult: MutableList<Int>): String {
    var finalString = ""
    queryResult.toSortedSet().forEach {
        finalString += "\n${arrayOfPeople[it]}"
    }
    return finalString
}

fun printAllPeople(arrayOfPeople: MutableList<String>) {
    arrayOfPeople.forEach {
        println(it)
    }
}
