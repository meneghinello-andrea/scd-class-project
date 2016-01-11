COMPILER						= pdflatex

CONFIGURATION					= configuration/acronymDatabase.tex \
								  configuration/glossaryDatabase.tex \
								  configuration/preamble.tex

COVER_FILE_NAME					= cover
COVER_DIRECTORY					= cover

REPORT_FILE_NAME				= master

CHAPTERS						= chapters/abstract/abstract.tex \
								  chapters/introduction/introduction.tex \
								  chapters/issues/issues.tex \
								  chapters/analysis/analysis.tex \
								  chapters/solution/solution.tex \
								  chapters/conclusion/conclusion.tex \
								  appendix/setup/setup.tex

.PHONY: clean clean_cover clean_master

$(REPORT_FILE_NAME).pdf: $(CONFIGURATION) $(COVER_FILE_NAME).pdf $(REPORT_FILE_NAME).tex $(CHAPTERS)
	@$(COMPILER) $(REPORT_FILE_NAME)
	@makeglossaries $(REPORT_FILE_NAME)
	@$(COMPILER) $(REPORT_FILE_NAME)
	@$(COMPILER) $(REPORT_FILE_NAME)

$(COVER_FILE_NAME).pdf: $(COVER_DIRECTORY)/$(COVER_FILE_NAME).tex
	@$(COMPILER) $(COVER_DIRECTORY)/$(COVER_FILE_NAME).tex
	@$(COMPILER) $(COVER_FILE_NAME)-frn.tex
	@$(COMPILER) $(COVER_DIRECTORY)/$(COVER_FILE_NAME).tex

clean: clean_cover clean_master

clean_cover:
	@rm -f $(COVER_FILE_NAME)-frn.log
	@rm -f $(COVER_FILE_NAME)-frn.pdf
	@rm -f $(COVER_FILE_NAME)-frn.tex
	@rm -f $(COVER_FILE_NAME).aux
	@rm -f $(COVER_FILE_NAME).log
	@rm -f $(COVER_FILE_NAME).pdf

clean_master:
	@rm -f $(REPORT_FILE_NAME).aux
	@rm -f $(REPORT_FILE_NAME).glg
	@rm -f $(REPORT_FILE_NAME).glo
	@rm -f $(REPORT_FILE_NAME).gls
	@rm -f $(REPORT_FILE_NAME).ist
	@rm -f $(REPORT_FILE_NAME).lof
	@rm -f $(REPORT_FILE_NAME).log
	@rm -f $(REPORT_FILE_NAME).lot
	@rm -f $(REPORT_FILE_NAME).out
	#@rm -f $(REPORT_FILE_NAME).pdf
	@rm -f $(REPORT_FILE_NAME).toc
	@rm -f texput.log