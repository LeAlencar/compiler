package main
import (
	"fmt"
)

func main() {
x := 10
y := 5
	if (x > y) {
		x = x + y
	fmt.Println(x)
	}
 else {
		y = y + x
	fmt.Println(y)
	}
	for i := 0; 		i < 3; 		i += 1 {
		x = x * 2
	fmt.Println(x)
	}
}

