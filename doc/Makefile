all: doc
doc: jpf-abstract.pdf

%.pdf: %.latex figure.mp
	mpost figure.mp
	pdflatex $<
