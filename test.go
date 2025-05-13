package main

import (
	"fmt"
)

func main() {
	s := "Hello, World!"
	fmt.Println(s)
	x := 10
	if x == 10 {
		fmt.Print("Escreva seu nome")
		fmt.Scanln(&leitura)
		x := 1
	}
	for {
		x := x + 1
		if x < 15 {
			break
		}
	}
	for i := 0; i < 10; i += 1 {
		x := x * 2
	}
}
