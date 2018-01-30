
import sys

def usage():
	print("python typeid_filter.py <Source Typeid> <Group ids> <Output File>")

def loadGroups(groups):
	
	out = set()
	
	with open(groups) as f:
		for line in f:
			line = line.rstrip()
			s = line.split("\t")
			
			if s[5] != "[]":
				s = s[5][1:-1].split(",")
				out = out.union([int(i) for i in s])
	
	return out

def loadItems(items):
	
	out = []
	
	with open(items) as f:
		for line in f:
			s = line.split("\t")
			out.append((int(s[0]), s[1]))
	
	return out

def main():
	
	if len(sys.argv) != 4:
		print("Incorrect number of arguments")
		usage()
		return
	
	if sys.argv[1] == sys.argv[3]:
		print("Source Typeid cannot equal Output File, please fix the arguments")
		usage()
		return
	
	groups = loadGroups(sys.argv[2])
	items = loadItems(sys.argv[1])
	
	valid = [i for i in items if i[0] in groups]
	
	with open(sys.argv[3], "w") as f:
		for item in valid:
			f.write("{0}\t{1}".format(item[0], item[1]))
	


if __name__ == "__main__":
	main()




