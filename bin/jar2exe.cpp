#include <stdio.h>
#include <stdlib.h>
#include <string>
 
int main(int argc, char *argv[]) {
	std::string args;
	for (int i = 0; i < argc; i++)
		args.append(argv[i]).append(" ");
	
    system(("java -jar jsimplex.jar " + args).c_str());
    return 0;
}
