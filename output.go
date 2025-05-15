package main

import (
	"fmt"
)

func main() {
	var x = 10
	x = x
	var y = 5
	y = y
	var z = x * y
	z = z
	fmt.Println(z)
}

func teste() {
	var j = 10
	j = j
	if j > 10 {
		j += 1
		if j > 15 {
			j += 2
		}
	} else {
		j -= 5
	}
	fmt.Println("hello")
	var k = j + 1
	k = k
}
