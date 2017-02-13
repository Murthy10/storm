\NeedsTeXFormat{LaTeX2e}
\ProvidesClass{Dissertate}[2014/03/25 v2.0 Dissertate Class]
\LoadClass[12pt, oneside, letterpaper]{book}

\RequirePackage{etoolbox}


\newcommand{\dnormalspacing}{1.2}
\newcommand{\dcompressedspacing}{1.0}


% Block quote with compressed spacing
\let\oldquote\quote
\let\endoldquote\endquote
\renewenvironment{quote}
    {\begin{spacing}{\dcompressedspacing}\oldquote}
    {\endoldquote\end{spacing}}

% Itemize with compressed spacing
\let\olditemize\itemize
\let\endolditemize\enditemize
\renewenvironment{itemize}
    {\begin{spacing}{\dcompressedspacing}\olditemize}
    {\endolditemize\end{spacing}}

% Enumerate with compressed spacing
\let\oldenumerate\enumerate
\let\endoldenumerate\endenumerate
\renewenvironment{enumerate}
    {\begin{spacing}{\dcompressedspacing}\oldenumerate}
    {\endoldenumerate\end{spacing}}

% Text layout.
\RequirePackage[width=5.75in, letterpaper]{geometry}
\usepackage{ragged2e}
\usepackage{tabu}
\usepackage{float}
\usepackage{caption}
\usepackage{listings}
\RaggedRight
\RequirePackage{graphicx}
\usepackage{fixltx2e}
\parindent 12pt
\RequirePackage{lettrine}
\RequirePackage{setspace}
\RequirePackage{verbatim}

% Fonts.
\RequirePackage{color}
\RequirePackage{xcolor}
\usepackage{hyperref}
\newcommand\fnurl[2]{%
\href{#2}{#1}\footnote{\url{#2}}%
}
\RequirePackage{url}
\RequirePackage{amssymb}
%\AtBeginEnvironment{tabular}{\addfontfeature{RawFeature=+tnum}}
\widowpenalty=300
\clubpenalty=300
\RequirePackage[labelfont={bf,sf,footnotesize,singlespacing},
                textfont={sf,footnotesize,singlespacing},
                justification={justified,RaggedRight},
                singlelinecheck=false,
                margin=0pt,
                figurewithin=chapter,
                tablewithin=chapter]{caption}
\renewcommand{\thefootnote}{\fnsymbol{footnote}}
\RequirePackage{microtype}


% Headings and headers.
\RequirePackage{fancyhdr}
\RequirePackage[tiny, md, sc]{titlesec}
\setlength{\headheight}{15pt}
\pagestyle{plain}
\RequirePackage{titling}

% Front matter.
\setcounter{tocdepth}{1}
\usepackage[titles]{tocloft}
\usepackage[titletoc,title]{appendix}
\renewcommand{\cftsecleader}{\cftdotfill{\cftdotsep}}
\renewcommand{\cftchapfont}{\normalsize \scshape}
\renewcommand\listfigurename{Listing of figures}
\renewcommand\listtablename{Listing of tables}

% Endmatter
\renewcommand{\setthesection}{\arabic{chapter}.A\arabic{section}}

% References.
\renewcommand\bibname{References}
\RequirePackage[super,comma,numbers]{natbib}
\renewcommand{\bibnumfmt}[1]{[#1]}
\RequirePackage[palatino]{quotchap}
\renewcommand*{\chapterheadstartvskip}{\vspace*{-0.5\baselineskip}}
\renewcommand*{\chapterheadendvskip}{\vspace{1.3\baselineskip}}

% An environment for paragraph-style section.
\providecommand\newthought[1]{%
   \addvspace{1.0\baselineskip plus 0.5ex minus 0.2ex}%
   \noindent\textsc{#1}%
}

% Align reference numbers so that they do not cause an indent.
\newlength\mybibindent
\setlength\mybibindent{0pt}
\renewenvironment{thebibliography}[1]
    {\chapter*{\bibname}%
     \@mkboth{\MakeUppercase\bibname}{\MakeUppercase\bibname}%
     \list{\@biblabel{\@arabic\c@enumiv}}
          {\settowidth\labelwidth{\@biblabel{999}}
           \leftmargin\labelwidth
            \advance\leftmargin\dimexpr\labelsep+\mybibindent\relax\itemindent-\mybibindent
           \@openbib@code
           \usecounter{enumiv}
           \let\p@enumiv\@empty
           \renewcommand\theenumiv{\@arabic\c@enumiv}}
     \sloppy
     \clubpenalty4000
     \@clubpenalty \clubpenalty
     \widowpenalty4000%
     \sfcode`\.\@m}
    {\def\@noitemerr
      {\@latex@warning{Empty `thebibliography' environment}}
     \endlist}

% Some definitions.
\def\title#1{\gdef\@title{#1}}
\def\author#1{\gdef\@author{#1}}
\def\advisor#1{\gdef\@advisor{#1}}
\def\degreeterm#1{\gdef\@degreeterm{#1}}
\def\university#1{\gdef\@university{#1}}
\def\universitycity#1{\gdef\@universitycity{#1}}
\def\universitystate#1{\gdef\@universitystate{#1}}


\definecolor{SchoolColor}{HTML}{003262} %berkeley blue
%\definecolor{chaptergrey}{HTML}{003262} % chapter numbers

% \definecolor{SchoolColor}{rgb}{0,0.19608,0.3843} %berkeley blue
%\definecolor{chaptergrey}{rgb}{0,0.19608,0.3843} % chapter numbers

% \definecolor{SchoolColor}{HTML}{0C77E8} %berkeley gold
\definecolor{chaptergrey}{HTML}{0C77E8} % chapter numbers

\hypersetup{
    colorlinks,
    citecolor=SchoolColor,
    filecolor=black,
    linkcolor=black,
    urlcolor=SchoolColor,
}

\renewcommand{\frontmatter}{
    \input{frontmatter/personalize}
    \maketitle
    % \copyrightpage
    \abstractpage
    \tableofcontents
    % \listoffigures % optional
    \setcounter{page}{1}
    \pagenumbering{arabic}
}

\renewcommand{\maketitle}{
    \thispagestyle{empty}
    \vspace*{\fill}
    \vspace{10pt}
    \begin{center}
    \Huge \textcolor{SchoolColor}{\@title} \normalsize \\
    \vspace{100pt}
    By\\
    \@author\\
    \vspace{12pt}
    \@university\\
    \vspace{50pt}
    \vspace{20pt}
    \vspace{50pt}
    \@degreeterm
    \end{center} \vspace*{\fill}
}



\newcommand{\copyrightpage}{
    \newpage
    \thispagestyle{empty}
    \vspace*{\fill}
    \scshape \noindent \small \copyright \small 2015\hspace{3pt}-- \author
    \noindent all rights reserved.
    \vspace*{\fill}
    \newpage
    \rm
}

\newcommand{\abstractpage}{
    \newpage
    \setcounter{page}{1}
    \pagenumbering{arabic}
    \pagestyle{fancy}
    \lhead{Advisor: \\\@advisor}
    \rhead{\@author\\[\baselineskip]}
    \renewcommand{\headrulewidth}{0.0pt}
    \vspace*{35pt}
    \begin{center}
    \Large \textcolor{SchoolColor}{\@title} \normalsize \\
    \vspace*{20pt}
    \scshape Abstract \\ \rm
    \end{center}
    \input{frontmatter/abstract}
    \vspace*{\fill}
    \newpage \lhead{} \rhead{}
    \cfoot{\thepage}
}
