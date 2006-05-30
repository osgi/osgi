%!PS-Adobe-3.0
%%Title: other.jsr211.fm
%%Creator: PScript5.dll Version 5.2
%%CreationDate: 5/30/2006 18:29:36
%%For: pkriens
%%BoundingBox: (atend)
%%DocumentNeededResources: (atend)
%%DocumentSuppliedResources: (atend)
%%DocumentData: Clean7Bit
%%TargetDevice: (Phaser 8400DP) (3015.102) 4
%%LanguageLevel: 3
mark {
/RRCustomProcs /ProcSet findresource (logjobinfo) known {
(pkriens) /RRCustomProcs /ProcSet findresource begin logusername end
(other.jsr211.fm) /RRCustomProcs /ProcSet findresource begin logfilename end
(other.jsr211.fm) /RRCustomProcs /ProcSet findresource begin logjobname end
/RRCustomProcs /ProcSet findresource begin logjobinfo end
}if
} stopped cleartomark
%%EndComments

%%BeginDefaults
%%ViewingOrientation: 1 0 0 1
%%EndDefaults

[{
%%BeginFeature: *JobPatchFile 1

% Default Definitions
  userdict /xerox$pagedevice <<
    /MediaClass null
    /MediaPosition null
    /PageSize [612 792]
    /TraySwitch true
    /ImagingBBox null
    /ProcessColorModel /DeviceCMYK
    /DeviceRenderingInfo currentpagedevice 1 index get 1 dict copy
    /PostRenderingEnhance false
  >> put

  userdict /xerox$set_job_type true put
  userdict /xerox$jobname (unnamed) put
  userdict /xerox$jobpassword () put
  userdict /xerox$jog 3 put
  userdict /xerox$smoothingDone false put
  userdict /xerox$pageSizeDone false put
  userdict /xerox$force_env false put

  userdict /xerox$env {
    userdict /xerox$force_env true put
  } put

  userdict /xerox$media_mismatch_code {
  } put

  userdict /xerox$setpagedevice {
    xerox$force_env {
      dup /MediaClass get null eq {
        dup /MediaClass (Envelope) put
      } if
    } if
    xerox$media_mismatch_code
    setpagedevice
  } put

  userdict /xerox$validate_jobname {
    {
      xerox$jobname length 0 eq {exit} if
      xerox$jobname 0 get 32 eq {
        userdict /xerox$jobname 
          xerox$jobname 1 xerox$jobname length 1 sub getinterval
          put
      }{
        exit
      } ifelse
    } loop
    xerox$jobname length 0 eq {
      userdict /xerox$jobname (unnamed) put
    } if
  } put

  userdict /xerox$pm_job
    false 99 -1 40 {
      userdict /xerox$str 5 string put
      xerox$str 0 (P) putinterval
      xerox$str exch 1 exch 2 string cvs putinterval
      xerox$str 3 (PS) putinterval
      userdict xerox$str cvn known or
      dup {exit} if
    } for
    put




%%EndFeature
} stopped cleartomark

%%BeginProlog
%%BeginResource: file Pscript_WinNT_ErrorHandler 5.0 0
/currentpacking where{pop/oldpack currentpacking def/setpacking where{pop false
setpacking}if}if/$brkpage 64 dict def $brkpage begin/prnt{dup type/stringtype
ne{=string cvs}if dup length 6 mul/tx exch def/ty 10 def currentpoint/toy exch
def/tox exch def 1 setgray newpath tox toy 2 sub moveto 0 ty rlineto tx 0
rlineto 0 ty neg rlineto closepath fill tox toy moveto 0 setgray show}bind def
/nl{currentpoint exch pop lmargin exch moveto 0 -10 rmoveto}def/=={/cp 0 def
typeprint nl}def/typeprint{dup type exec}readonly def/lmargin 72 def/rmargin 72
def/tprint{dup length cp add rmargin gt{nl/cp 0 def}if dup length cp add/cp
exch def prnt}readonly def/cvsprint{=string cvs tprint( )tprint}readonly def
/integertype{cvsprint}readonly def/realtype{cvsprint}readonly def/booleantype
{cvsprint}readonly def/operatortype{(--)tprint =string cvs tprint(-- )tprint}
readonly def/marktype{pop(-mark- )tprint}readonly def/dicttype{pop
(-dictionary- )tprint}readonly def/nulltype{pop(-null- )tprint}readonly def
/filetype{pop(-filestream- )tprint}readonly def/savetype{pop(-savelevel- )
tprint}readonly def/fonttype{pop(-fontid- )tprint}readonly def/nametype{dup
xcheck not{(/)tprint}if cvsprint}readonly def/stringtype{dup rcheck{(\()tprint
tprint(\))tprint}{pop(-string- )tprint}ifelse}readonly def/arraytype{dup rcheck
{dup xcheck{({)tprint{typeprint}forall(})tprint}{([)tprint{typeprint}forall(])
tprint}ifelse}{pop(-array- )tprint}ifelse}readonly def/packedarraytype{dup
rcheck{dup xcheck{({)tprint{typeprint}forall(})tprint}{([)tprint{typeprint}
forall(])tprint}ifelse}{pop(-packedarray- )tprint}ifelse}readonly def/courier
/Courier findfont 10 scalefont def end errordict/handleerror{systemdict begin
$error begin $brkpage begin newerror{/newerror false store vmstatus pop pop 0
ne{grestoreall}if errorname(VMerror)ne{showpage}if initgraphics courier setfont
lmargin 720 moveto errorname(VMerror)eq{userdict/ehsave known{clear userdict
/ehsave get restore 2 vmreclaim}if vmstatus exch pop exch pop PrtVMMsg}{
(ERROR: )prnt errorname prnt nl(OFFENDING COMMAND: )prnt/command load prnt
$error/ostack known{nl nl(STACK:)prnt nl nl $error/ostack get aload length{==}
repeat}if}ifelse systemdict/showpage get exec(%%[ Error: )print errorname
=print(; OffendingCommand: )print/command load =print( ]%%)= flush}if end end
end}dup 0 systemdict put dup 4 $brkpage put bind readonly put/currentpacking
where{pop/setpacking where{pop oldpack setpacking}if}if
%%EndResource
userdict /Pscript_WinNT_Incr 230 dict dup begin put
%%BeginResource: file Pscript_FatalError 5.0 0
userdict begin/FatalErrorIf{{initgraphics findfont 1 index 0 eq{exch pop}{dup
length dict begin{1 index/FID ne{def}{pop pop}ifelse}forall/Encoding
{ISOLatin1Encoding}stopped{StandardEncoding}if def currentdict end
/ErrFont-Latin1 exch definefont}ifelse exch scalefont setfont counttomark 3 div
cvi{moveto show}repeat showpage quit}{cleartomark}ifelse}bind def end
%%EndResource
userdict begin/PrtVMMsg{vmstatus exch sub exch pop gt{[
(This job requires more memory than is available in this printer.)100 500
(Try one or more of the following, and then print again:)100 485
(For the output format, choose Optimize For Portability.)115 470
(In the Device Settings page, make sure the Available PostScript Memory is accurate.)
115 455(Reduce the number of fonts in the document.)115 440
(Print the document in parts.)115 425 12/Times-Roman showpage
(%%[ PrinterError: Low Printer VM ]%%)= true FatalErrorIf}if}bind def end
version cvi 2016 ge{/VM?{pop}bind def}{/VM? userdict/PrtVMMsg get def}ifelse
105000 VM?
%%BeginResource: file Pscript_Win_Basic 5.0 0
/d/def load def/,/load load d/~/exch , d/?/ifelse , d/!/pop , d/`/begin , d/^
/index , d/@/dup , d/+/translate , d/$/roll , d/U/userdict , d/M/moveto , d/-
/rlineto , d/&/currentdict , d/:/gsave , d/;/grestore , d/F/false , d/T/true ,
d/N/newpath , d/E/end , d/Ac/arc , d/An/arcn , d/A/ashow , d/D/awidthshow , d/C
/closepath , d/V/div , d/O/eofill , d/L/fill , d/I/lineto , d/-c/curveto , d/-M
/rmoveto , d/+S/scale , d/Ji/setfont , d/Lc/setlinecap , d/Lj/setlinejoin , d
/Lw/setlinewidth , d/Lm/setmiterlimit , d/sd/setdash , d/S/show , d/LH/showpage
, d/K/stroke , d/W/widthshow , d/R/rotate , d/L2? false/languagelevel where{pop
languagelevel 2 ge{pop true}if}if d L2?{/xS/xshow , d/yS/yshow , d/zS/xyshow ,
d}if/b{bind d}bind d/bd{bind d}bind d/xd{~ d}bd/ld{, d}bd/bn/bind ld/lw/Lw ld
/lc/Lc ld/lj/Lj ld/sg/setgray ld/ADO_mxRot null d/self & d/OrgMx matrix
currentmatrix d/reinitialize{: OrgMx setmatrix[/TextInit/GraphInit/UtilsInit
counttomark{@ where{self eq}{F}?{cvx exec}{!}?}repeat cleartomark ;}b
/initialize{`{/Pscript_Win_Data where{!}{U/Pscript_Win_Data & put}?/ADO_mxRot ~
d/TextInitialised? F d reinitialize E}{U/Pscript_Win_Data 230 dict @ ` put
/ADO_mxRot ~ d/TextInitialised? F d reinitialize}?}b/terminate{!{& self eq
{exit}{E}?}loop E}b/suspend/terminate , d/resume{` Pscript_Win_Data `}b U `
/lucas 21690 d/featurebegin{countdictstack lucas[}b/featurecleanup{stopped
{cleartomark @ lucas eq{! exit}if}loop countdictstack ~ sub @ 0 gt{{E}repeat}
{!}?}b E/snap{transform 0.25 sub round 0.25 add ~ 0.25 sub round 0.25 add ~
itransform}b/dsnap{dtransform round ~ round ~ idtransform}b/nonzero_round{@ 0.5
ge{round}{@ -0.5 lt{round}{0 ge{1}{-1}?}?}?}b/nonzero_dsnap{dtransform
nonzero_round ~ nonzero_round ~ idtransform}b U<04>cvn{}put/rr{1 ^ 0 - 0 ~ -
neg 0 - C}b/irp{4 -2 $ + +S fx 4 2 $ M 1 ^ 0 - 0 ~ - neg 0 -}b/rp{4 2 $ M 1 ^ 0
- 0 ~ - neg 0 -}b/solid{[]0 sd}b/g{@ not{U/DefIf_save save put}if U/DefIf_bool
2 ^ put}b/DefIf_El{if U/DefIf_bool get not @{U/DefIf_save get restore}if}b/e
{DefIf_El !}b/UDF{L2?{undefinefont}{!}?}b/UDR{L2?{undefineresource}{! !}?}b
/freeVM{/Courier findfont[40 0 0 -40 0 0]makefont Ji 2 vmreclaim}b/hfRedefFont
{findfont @ length dict `{1 ^/FID ne{d}{! !}?}forall & E @ ` ~{/CharStrings 1
dict `/.notdef 0 d & E d}if/Encoding 256 array 0 1 255{1 ^ ~/.notdef put}for d
E definefont !}bind d/hfMkCIDFont{/CIDFont findresource @ length 2 add dict `{1
^ @/FID eq ~ @/XUID eq ~/UIDBase eq or or{! !}{d}?}forall/CDevProc ~ d/Metrics2
16 dict d/CIDFontName 1 ^ d & E 1 ^ ~/CIDFont defineresource ![~]composefont !}
bind d
%%EndResource
%%BeginResource: file Pscript_Win_Utils_L2 5.0 0
/rf/rectfill , d/fx{1 1 dtransform @ 0 ge{1 sub 0.5}{1 add -0.5}? 3 -1 $ @ 0 ge
{1 sub 0.5}{1 add -0.5}? 3 1 $ 4 1 $ idtransform 4 -2 $ idtransform}b/BZ{4 -2 $
snap + +S fx rf}b/rs/rectstroke , d/rc/rectclip , d/UtilsInit{currentglobal{F
setglobal}if}b/scol{! setcolor}b/colspA/DeviceGray d/colspABC/DeviceRGB d
/colspRefresh{colspABC setcolorspace}b/SetColSpace{colspABC setcolorspace}b
/resourcestatus where{!/ColorRendering/ProcSet resourcestatus{! ! T}{F}?}{F}?
not{/ColorRendering<</GetHalftoneName{currenthalftone @/HalftoneName known{
/HalftoneName get}{!/none}?}bn/GetPageDeviceName{currentpagedevice @
/PageDeviceName known{/PageDeviceName get @ null eq{!/none}if}{!/none}?}bn
/GetSubstituteCRD{!/DefaultColorRendering/ColorRendering resourcestatus{! !
/DefaultColorRendering}{(DefaultColorRendering*){cvn exit}127 string
/ColorRendering resourceforall}?}bn>>/defineresource where{!/ProcSet
defineresource !}{! !}?}if/buildcrdname{/ColorRendering/ProcSet findresource `
mark GetHalftoneName @ type @/nametype ne ~/stringtype ne and{!/none}if(.)
GetPageDeviceName @ type @/nametype ne ~/stringtype ne and{!/none}if(.)5 ^ 0 5
-1 1{^ length add}for string 6 1 $ 5 ^ 5{~ 1 ^ cvs length 1 ^ length 1 ^ sub
getinterval}repeat ! cvn 3 1 $ ! ! E}b/definecolorrendering{~ buildcrdname ~
/ColorRendering defineresource !}b/findcolorrendering where{!}{
/findcolorrendering{buildcrdname @/ColorRendering resourcestatus{! ! T}{
/ColorRendering/ProcSet findresource ` GetSubstituteCRD E F}?}b}?
/selectcolorrendering{findcolorrendering !/ColorRendering findresource
setcolorrendering}b/G2UBegin{findresource/FontInfo get/GlyphNames2Unicode get
`}bind d/G2CCBegin{findresource/FontInfo get/GlyphNames2HostCode get `}bind d
/G2UEnd{E}bind d/AddFontInfoBegin{/FontInfo 8 dict @ `}bind d/AddFontInfo{
/GlyphNames2Unicode 16 dict d/GlyphNames2HostCode 16 dict d}bind d
/AddFontInfoEnd{E d}bind d/T0AddCFFMtx2{/CIDFont findresource/Metrics2 get ` d
E}bind d
%%EndResource
end
mark {/RRCustomProcs /ProcSet findresource begin
/logjobinfo where
{pop
/logclientjobid where {pop (8pkriens                                16293591) logclientjobid} if
logjobinfo
}if
end
 } stopped cleartomark
%%EndProlog

%%BeginSetup
userdict /xerox$NT true put
userdict /xerox$jobpassword <> put
userdict /xerox$jobname <6f746865722e6a73723231312e666d> put
statusdict begin (%%[ ProductName: ) print product print ( ]%%)= flush end
[ 1 0 0 1 0 0 ] false Pscript_WinNT_Incr dup /initialize get exec
featurebegin{
%%BeginNonPPDFeature: JobTimeout 0
0 /languagelevel where{pop languagelevel}{1}ifelse 2 ge{1 dict dup/JobTimeout  4 -1 roll put setuserparams}{statusdict/setjobtimeout get exec}ifelse
%%EndNonPPDFeature
}featurecleanup

featurebegin{
%%BeginNonPPDFeature: WaitTimeout 120
120 /languagelevel where{pop languagelevel}{1}ifelse 2 ge{1 dict dup/WaitTimeout 4 -1 roll put setuserparams}{statusdict/waittimeout 3 -1 roll put}ifelse
%%EndNonPPDFeature
}featurecleanup

featurebegin{
%%BeginFeature: *XRXJobName Set

%%EndFeature
}featurecleanup
featurebegin{
%%BeginFeature: *XRXJobPassword Set

%%EndFeature
}featurecleanup
featurebegin{
%%BeginFeature: *MediaType Paper

   xerox$pagedevice /MediaClass (Paper) put

%%EndFeature
}featurecleanup
featurebegin{
%%BeginFeature: *XRXJobType Normal


%%EndFeature
}featurecleanup
featurebegin{
%%BeginFeature: *Duplex None

   <</Duplex false /Tumble false>> xerox$pagedevice copy pop

%%EndFeature
}featurecleanup
featurebegin{
%%BeginFeature: *OutputMode Enhanced

   <<
     /HWResolution [563 400]
   >> xerox$pagedevice copy pop

%%EndFeature
}featurecleanup
featurebegin{
%%BeginFeature: *XRXColor Automatic

   <<
     /DeviceRenderingInfo xerox$pagedevice 1 index get 1 dict copy
     dup /VirtualColorDevice <<
       /Type 3
       /ColorTransform /Automatic
     >> put
   >> xerox$pagedevice copy pop

%%EndFeature
}featurecleanup
featurebegin{
%%BeginFeature: *XRXMismatch PromptTypeMismatch


%%EndFeature
}featurecleanup
featurebegin{
%%BeginFeature: *XRXSlipSheetSource Tray1

   0 /RRCustomProcs /ProcSet findresource /setslipsheetsource get exec

%%EndFeature
}featurecleanup
featurebegin{
%%BeginFeature: *Slipsheet None

   xerox$pagedevice /SlipSheet 0 put

%%EndFeature
}featurecleanup
featurebegin{
%%BeginFeature: *XRXCoverPageSource Tray1

   userdict /xerox$CoverPageSource 0 put

%%EndFeature
}featurecleanup
featurebegin{
%%BeginFeature: *XRXCoverPage None

%%EndFeature
}featurecleanup
featurebegin{
%%BeginFeature: *XRXImageSmoothing False

   false /RRCustomProcs /ProcSet findresource /setforceinterpolate get exec
   xerox$pageSizeDone {xerox$pagedevice xerox$setpagedevice} if
   userdict /xerox$smoothingDone true put

%%EndFeature
}featurecleanup
featurebegin{
%%BeginFeature: *InstalledMemory 256Meg

%%EndFeature
}featurecleanup
featurebegin{
%%BeginFeature: *XRXOptionFeatureSet DP

%%EndFeature
}featurecleanup
featurebegin{
%%BeginFeature: *XRXOptionTrays None

%%EndFeature
}featurecleanup
featurebegin{
%%BeginFeature: *XRXOptionHD False

%%EndFeature
}featurecleanup
featurebegin{
%%BeginFeature: *PageSize Letter

  <<
    /PageSize [612 792]
  >> xerox$pagedevice copy xerox$setpagedevice

%%EndFeature
}featurecleanup
featurebegin{
%%BeginFeature: *Resolution 525dpi

%%EndFeature
}featurecleanup
1 setlinecap 1 setlinejoin
/mysetup [ 72 525 V 0 0 -72 525 V 15.00094 777.99969 ] def 
%%EndSetup

userdict begin /ehsave save def end
%%EndPageComments
%%BeginPageSetup
/DeviceRGB dup setcolorspace /colspABC exch def
mysetup concat colspRefresh
%%EndPageSetup

30000 VM?
Pscript_WinNT_Incr begin
%%BeginResource: file Pscript_WinNT_Compat 5.0 0
userdict/Pscript_WinNT_Compat 19 dict dup begin/bd{bind def}bind def/ld{load
def}bd/$x matrix def/ANSIVec[16#0/grave 16#1/acute 16#2/circumflex 16#3/tilde
16#4/macron 16#5/breve 16#6/dotaccent 16#7/dieresis 16#8/ring 16#9/cedilla 16#A
/hungarumlaut 16#B/ogonek 16#C/caron 16#D/dotlessi 16#27/quotesingle 16#60
/grave 16#7C/bar 16#82/quotesinglbase 16#83/florin 16#84/quotedblbase 16#85
/ellipsis 16#86/dagger 16#87/daggerdbl 16#88/circumflex 16#89/perthousand 16#8A
/Scaron 16#8B/guilsinglleft 16#8C/OE 16#91/quoteleft 16#92/quoteright 16#93
/quotedblleft 16#94/quotedblright 16#95/bullet 16#96/endash 16#97/emdash 16#98
/tilde 16#99/trademark 16#9A/scaron 16#9B/guilsinglright 16#9C/oe 16#9F
/Ydieresis 16#A0/space 16#A1/exclamdown 16#A4/currency 16#A5/yen 16#A6
/brokenbar 16#A7/section 16#A8/dieresis 16#A9/copyright 16#AA/ordfeminine 16#AB
/guillemotleft 16#AC/logicalnot 16#AD/hyphen 16#AE/registered 16#AF/macron
16#B0/degree 16#B1/plusminus 16#B2/twosuperior 16#B3/threesuperior 16#B4/acute
16#B5/mu 16#B6/paragraph 16#B7/periodcentered 16#B8/cedilla 16#B9/onesuperior
16#BA/ordmasculine 16#BB/guillemotright 16#BC/onequarter 16#BD/onehalf 16#BE
/threequarters 16#BF/questiondown 16#C0/Agrave 16#C1/Aacute 16#C2/Acircumflex
16#C3/Atilde 16#C4/Adieresis 16#C5/Aring 16#C6/AE 16#C7/Ccedilla 16#C8/Egrave
16#C9/Eacute 16#CA/Ecircumflex 16#CB/Edieresis 16#CC/Igrave 16#CD/Iacute 16#CE
/Icircumflex 16#CF/Idieresis 16#D0/Eth 16#D1/Ntilde 16#D2/Ograve 16#D3/Oacute
16#D4/Ocircumflex 16#D5/Otilde 16#D6/Odieresis 16#D7/multiply 16#D8/Oslash
16#D9/Ugrave 16#DA/Uacute 16#DB/Ucircumflex 16#DC/Udieresis 16#DD/Yacute 16#DE
/Thorn 16#DF/germandbls 16#E0/agrave 16#E1/aacute 16#E2/acircumflex 16#E3
/atilde 16#E4/adieresis 16#E5/aring 16#E6/ae 16#E7/ccedilla 16#E8/egrave 16#E9
/eacute 16#EA/ecircumflex 16#EB/edieresis 16#EC/igrave 16#ED/iacute 16#EE
/icircumflex 16#EF/idieresis 16#F0/eth 16#F1/ntilde 16#F2/ograve 16#F3/oacute
16#F4/ocircumflex 16#F5/otilde 16#F6/odieresis 16#F7/divide 16#F8/oslash 16#F9
/ugrave 16#FA/uacute 16#FB/ucircumflex 16#FC/udieresis 16#FD/yacute 16#FE/thorn
16#FF/ydieresis]def currentdict{dup type/operatortype eq{[exch]cvx def}{pop
pop}ifelse}forall/initialize{currentdict exch begin begin}bind def/terminate{
/@FL where not{pop end end}{pop}ifelse}bind def/suspend/terminate load def
/resume/initialize load def/RS{/pagesave where{pop pagesave restore}{$x matrix
invertmatrix concat}ifelse}def/SS{/pagesave save def}def/CB{pop pop pop pop}def
/B{pop pop pop pop}def/:/gsave load def/;/grestore load def/N/newpath load def
end put
%%EndResource
end reinitialize
Pscript_WinNT_Compat begin /$x mysetup def end
Pscript_WinNT_Incr dup /suspend get exec
Pscript_WinNT_Compat dup /initialize get exec
%%BeginDocument: Pscript_Win_PassThrough
% Copyright (c) 1986-1995 Frame Technology Corporation.
% DO NOT CHANGE THE FIRST LINE; Adobe PS 5.1.2 depends on it.
/FMcmyk 100 dict def
/FmBD{bind def}bind def
/FmLD{load def}FmBD
/FMc {
 FMcmyk length FMcmyk maxlength ge { /FMcmyk FMcmyk dup length dup add dict copy def } if
 4 array astore 4 1 roll 8 bitshift add 8 bitshift add exch FMcmyk 3 1 roll put
}FmBD
/setcmykcolor where { pop
 /sc where { pop /sc load 0 get /scignore eq {
  /FMsc /sc FmLD
  /sc { 3 copy 8 bitshift add 8 bitshift add FMcmyk 1 index known
   { FMcmyk exch get aload pop setcmykcolor pop pop pop } { pop FMsc } ifelse
  }FmBD
 } if } if
} if
mark { /S load
 dup 0 get /PenW eq { dup 1 get /sl load eq {
  dup 0 { PenW .75 sub sl } bind put 1 /exec load put
 } if } if
} stopped cleartomark       
/FmX matrix defaultmatrix def
/FmDC {transform FmX defaultmatrix itransform cvi exch cvi exch} def
/FmBx { dup 3 index lt {3 1 roll exch} if 
 1 index 4 index lt {4 -1 roll 3 1 roll exch 4 1 roll} if
}FmBD
/FmPD/cleartomark FmLD
/FmPD2/cleartomark FmLD
/FmPD4/cleartomark FmLD
/FmPT/pop FmLD
/FmPA{pop pop pop}FmBD
/FmND{pop pop pop}FmBD
systemdict /pdfmark known systemdict /currentdistillerparams known and {
  /FmPD/pdfmark FmLD
  currentdistillerparams /CoreDistVersion get 2000 ge {
    /FmPD2/pdfmark FmLD
    % FmPD4 is like FmPD but for Acrobat 4.05-specific PDF
    /FmPD4U true def
    currentdistillerparams /CoreDistVersion get 4050 ge 
    {
      /FmPD4 /pdfmark load def
    }
    {
      /FmPD4
      { FmPD4U 
	{(%%[Acrobat Distiller 4.05 and up is required to generate Logical PDF Structure]%%) = 
	  (%%[Logical PDF Structure is not generated.]%%) = flush
	} if
	/FmPD4U false def
	cleartomark
      } FmBD
    } ifelse


% Procedure FmPA used to define named destinations for all paragraphs (and elements). As
% a result, the produced PDF files were very large in size, because they contained numerous
% named destinations that were not actually used. 
% In FrameMaker 5.5.6 FmND procedure was added to allow to distinguish between 
% named destinations that were definitely used and all other named destinations.
% The users were given opportunity to change the definition of the FmPA procedure,
% so that it either directed Distiller to produce or to not produce named destinations
% (under fmCG switch).
% FmND always produced a named destination, but FmPA produced a named destination onlY
% when fmCG switch was set to 'True'.
% FrameMaker 5.5.6 used very simplistic method to determine whether a named destination
% was actually used or not.
% FrameMaker 6.0 and up uses a much more elaborate method to determine whether a 
% named destination is actually used or not. It also has an improved "Acrobat Setup" dialog,
% which allows to specify the user's preference, whether to create PDF files with named
% destinations for all paragraphs, or Wonly those named destinations that are used.
% Therefore, there is no need for fmCG switch in FrameMaker 6.0 and up. Now FmND procedure
% defines a named destination, and FmPA procedure does nothing. Sophisticated users still 
% have ability to modify definition of FmPA if they wish so.

	  /fmCG true def 

	  /FmND
	  { mark exch /Dest exch 5 3 roll /View [ /XYZ 5 -2 roll FmDC null ] /DEST FmPD 
	  }FmBD

	  /FmPA 
	  { fmCG
	    { pop pop pop }
	    { FmND } ifelse
	  } FmBD
 } if
} if
: N : N /setdistillerparams where{pop 1 dict dup begin/AutoRotatePages/None def end setdistillerparams}if
[/CropBox[469 5266 FmDC 3776 305 FmDC FmBx]/PAGE FmPD
[/Dest/P.5/DEST FmPD2
[
/Creator (FrameMaker 6.0)
/CreationDate (D:20020530121705)
/ModDate (D:20060530182920)
/DOCINFO FmPD2
[/Dest/F/DEST FmPD2
[/Dest/G1212549/Title(2 JSR Interactions)/Count 3/OUT FmPD2
[/Dest/G1379070/Title(2.1 Introduction)/OUT FmPD2
[/Dest/G1379072/Title(2.2 JSR 211 Content Handling)/Count 4/OUT FmPD2
[/Dest/G1379074/Title(2.2.1 Content Handler API)/OUT FmPD2
[/Dest/G1379078/Title(2.2.2 Content Handler Identification)/OUT FmPD2
[/Dest/G1379085/Title(2.2.3 Content Handler Access Control)/OUT FmPD2
[/Dest/G1379089/Title(2.2.4 Method Descriptions)/OUT FmPD2
[/Dest/G1379111/Title(2.3 References)/OUT FmPD2
[ /PageMode /UseOutlines /Page 1 /View [/XYZ null null null] /DOCVIEW FmPD2
: N ; 1338 2377/M9.70514.Heading2.221.Content.Handler.API FmND
: N ; 1338 2377/I1.1385697 FmND
: N ; [/Rect[3297 5092 3398 5010]/Border[0 0 0]/Dest/M21.9.last.page/Action/GoToR/File(meg/book.pdf)/LNK FmPD2
: N ; [/Rect[1463 2068 2289 1986]/Border[0 0 0]/Dest/G1379112/LNK FmPD2
: N [/Title(A)/Rect[753 4965 3466 633]/ARTICLE FmPD2
; ; : N : N 
%%EndDocument

Pscript_WinNT_Compat dup /suspend get exec
Pscript_WinNT_Incr dup /resume get exec
/DeviceRGB dup setcolorspace /colspABC exch def
0 0 0 1 scol 11409 VM?
Pscript_WinNT_Incr begin
%%BeginResource: file Pscript_T42Hdr 5.0 0
/asc42 0.0 d/sF42{/asc42 ~ d Ji}bind d/bS42{0 asc42 -M}bind d/eS42{0 asc42 neg
-M}b/Is2015?{version cvi 2015 ge}bind d/AllocGlyphStorage{Is2015?{!}{{string}
forall}?}bind d/Type42DictBegin{25 dict `/FontName ~ d/Encoding ~ d 4 array
astore cvx/FontBBox ~ d/PaintType 0 d/FontType 42 d/FontMatrix[1 0 0 1 0 0]d
/CharStrings 256 dict `/.notdef 0 d & E d/sfnts}bind d/Type42DictEnd{& @
/FontName get ~ definefont ! E}bind d/RDS{string currentfile ~ readstring !}
executeonly d/PrepFor2015{Is2015?{/GlyphDirectory 16 dict d sfnts 0 get @ 2 ^
(glyx)putinterval 2 ^(locx)putinterval ! !}{! !}?}bind d/AddT42Char{Is2015?
{findfont/GlyphDirectory get ` d E ! !}{findfont/sfnts get 4 ^ get 3 ^ 2 ^
putinterval ! ! ! !}?}bind d/IDStrNull{1 add 2 mul @ string 0 1 3 ^ 1 sub{1 ^ ~
0 put}for ~ !}bind d/IDStr{@ 1 add 2 mul string 0 1 3 ^{1 ^ ~ @ 2 mul ~ 3 copy
256 idiv put ~ 1 add ~ 256 mod put}for ~ !}bind d/IDStr2{~ @ 1 add 2 mul string
0 1 3 ^{1 ^ ~ @ 2 mul ~ 5 ^ add 3 copy 256 idiv put ~ 1 add ~ 256 mod put}for ~
! ~ !}bind d/CIDT42Begin{25 dict `/CDevProc ~ d/CIDMap ~ d/CIDCount ~ d
/CIDSystemInfo 3 dict @ ` 3 -1 $/Supplement ~ d 3 -1 $/Ordering ~ d 3 -1 $
/Registry ~ d E d/CIDFontName ~ d/Encoding ~ d 4 array astore cvx/FontBBox ~ d
/CharStrings 2 dict @ `/.notdef 0 d E d/GDBytes 2 d/CIDFontType 2 d/FontType 42
d/PaintType 0 d/FontMatrix[1 0 0 1 0 0]d/Metrics2 16 dict d/sfnts}bind d
/CIDT42End{CIDFontName & E/CIDFont defineresource !}bind d/T42CIDCP32K{/CIDFont
findresource @ length dict copy @/FID undef `/CIDFontName ~ d/CIDMap ~ d
/CIDCount ~ d/Metrics2 8 dict d CIDFontName & E/CIDFont defineresource !}bind d
/T42CIDCPR{/CIDFont findresource @ length dict copy @/FID undef `/CIDFontName ~
d &/CDevProc known{[/CDevProc , @ type/operatortype ne{/exec cvx}if/! cvx/! cvx
/! cvx/! cvx 5/^ cvx 5/^ cvx 0.0 0.0]cvx}{{! ! ! ! ! 5 ^ 5 ^ 0.0 0.0}}? bind
readonly/CDevProc ~ d CIDFontName & E/CIDFont defineresource !}bind d
/T0AddT42Char{/CIDFont findresource/GlyphDirectory get ` d E ! !}bind d
/T0AddT42Mtx2{/CIDFont findresource/Metrics2 get ` d E}bind d/UpdateCIDMap{
/CIDFont findresource/CIDMap get 3 1 $ putinterval}d/AddXUID{version cvi 3011
ge{/XUID ~ d}{!}?}bind d/hfDef42CID{/CIDFont findresource @ length dict copy @
/FID undef `/CIDFontName 2 ^ d @ type @/booleantype ne{/arraytype eq{/CDevProc
~ d}{matrix @ 4 4 -1 $ put/FontMatrix ~ d}?}{! !}? & E/CIDFont defineresource
!}bind d/hfDefRT42CID{/CIDFont findresource @ length dict copy @/FID undef `
/CIDFontName 1 ^ d &/CDevProc known{[/CDevProc , @ type/operatortype ne{/exec
cvx}if/! cvx/! cvx/! cvx/! cvx 5/^ cvx 5/^ cvx 0.0 0.0]cvx}{{! ! ! ! ! 5 ^ 5 ^
0.0 0.0}}? bind readonly/CDevProc ~ d & E/CIDFont defineresource !}bind d
%%EndResource
end reinitialize
-0.059 -0.293 1.168 0.922
 256 array 0 1 255 {1 index exch /.notdef put} for  /TTE152A528t00
Type42DictBegin
[<00010000000a000a000a000a63767420597df929000000ac0000002a6670
676d8333c24f000000d800000014676c7966e81500e100000958000084ec
68656164d260dd39000000ec000000366868656107f00414000001240000
0024686d7478a7741f4100000148000003dc6c6f6361003faf1e00000524
000003e06d61787001930264000009040000002070726570c503cc0e0000
09240000003467646972000000000000000000000000fff501e402a402df
004800410044003c5a671206d2b86a18f82a61a30e40eed2803a2755a287
0001000d00004001002c764520b003254523616818236860442d00010000
00010000b42fea725f0f3cf5000003e800000000b77dccf100000000b77d
ccf1ffc1feda0490039c00000003000200010000000000010000039cfeda
000004b8ffc1ff8a04900001000000000000000000000000000000f701f4
003f0000000000c8000000c8000000d80030011e00280202001f01f40034
034b002802640034009a00280125003f0125001b01fb002001eb002300c8
001d0154001e00c80028012a001701ea0028018b001601c6003401b50024
01d5001801af002201d6003301b4003001d6003501d6002d00c8002800c8
00190173002801db00230173002801ae001b03e9002d0203000e01bf003a
0229002a0267003a01aa003a0186003a0255002a0260003a00c7003a00c7
000e01e7003a017f003a02e2003a024e003a0298002a019e003a0298002a
01c2003a01d0002201df0012024400340203000e030a000e01ec000a01e2
001001ef0025013e004601230016013e001c012c0006018cfff9012cfff5
019f002601d0003a0183002201cf002201b7002200ff000f01b4001d01cd
003a00c4002c00c40014019b003a00c4003a02d1002201cd002201da0022
01cf002201ce0022011c002201610020012a000e01cd00370193000c0286
000c017c00100193000c017e001a0182001500c7003a0182001a0154001c
0203000e0203000e0229002a01aa003a024e003a0298002a02440034019f
0026019f0026019f0026019f0026019f0026019f00260183002201b70022
01b7002201b7002201b7002200c4003500c4ffc100c4ffd200c4ffd701cd
002201da002201da002201da002201da002201da002201cd003701cd0037
01cd003701cd003701d000280136001d0190002201f40037016500200113
003201cc000f0202003a02d3003202d3003202ef001a012c0069012c000b
02d0000c0298002a0295000f01e2001001cd0037015a0034015a001902ac
002601da0022015e001a00d800300160001c01f4ffec0222001e0222001e
023c002800c800030203000e0203000e0298002a0311002a0304002201f4
001e03e8001e016600280166001d00c8002800c8001d01eb00230193000c
01e20010023a0011013c001e013c001e01d0002800c8002800c8001d0166
001d04b800280203000e01aa003a0203000e01aa003a01aa003400c70036
00c7ffd300c7ffd800c7ffc20298002a0298002a0298002a024400340244
003402440034012c0006012c0008012c0004012c003401d0002201610020
00e100460267fff201cf002201e200100193000c019e003f01cf003a01eb
002301900043019000480190004c019000430190002a0190002a00000003
017ffffc00c4fff001ef0025017e001a01e0000f0295000f017300280173
002e02c0000f01b4000f02bd000f02d3003201f4ffe80355003601c50006
0154001c028b000f020b000f012a001701b4000f01b4000f03e8000000c4
003a012c000a012c005a012c0029012cffdf012c004b012c000600000000
0000007c0000007c0000007c0000007c00000106000001860000028e0000
0384000004960000059e000005fc00000676000006f0000007c80000085c
000008d00000093600000998000009f600000a8e00000b2200000bc40000
0c8e00000d4a00000dfa00000eae00000f2800000ffc000010ae000010c6
000010de0000113a000011ce00001228000012ec0000142a000014bc0000
159a00001636000016d60000178400001820000018d800001994000019fe
00001a6800001b0e00001b9000001c4800001cec00001d9600001e460000
1f0800001fce0000208800002112000021b80000222e000022dc00002384
00002414000024b00000253e0000259c00002628000026380000268e0000
26e4000027b000002862000028fa000029c400002a8200002b3a00002c52
00002d0600002da000002e3600002eda00002f440000302a000030d80000
31760000323a000032f4000033840000343c000034ec0000359800003614
000036c200003762000037ee0000388c00003946000039ae00003a680000
3a7a00003a9200003b7000003c5000003c6800003c8000003c9800003cb0
00003cc600003cdc00003cf200003d0800003d1e00003e3400003f060000
3f1c00003f3200003f4800003f5e00003f7400003f8a00003fa000003fb6
00003fcc00003fe200003ff80000400e000040240000403a000040500000
40660000407c0000409200004144000041d2000042a0000043680000445c
000044be0000456400004652000047680000485000004968000049bc0000
4a4600004b2a00004c1c00004d4e00004e5600004f1400004fda00005072
0000519a0000527400005338000053c000005432000054ee000055680000
55e200005602000056020000561a000056320000564a0000574800005842
000058aa00005912000059c000005a7000005ae600005af800005bba0000
5bd000005be800005cf400005d4e00005da800005ea000005eb200005ec2
00005f6e000060d4000060ec000061040000611c000061340000614c0000
61640000617c00006194000061ac000061c4000061dc000061f40000620c
000062240000623c000062960000631e00006388000064060000641e0000
6434000064b80000658800006670000066880000669e0000675600006820
0000687600006906000069a600006a6a00006b7400006ca200006e040000
6e0400006eb600006f5000006f6800006f7e00007094000071ca00007252
000072da000073fe000074da000075f8000076fa0000778400007ae60000
7b6000007b7a00007c8a00007d9600007da600007e8000007f580000822a
0000829a0000830e00008320000083b20000842400008492000084ec0001
000000f700e20007003c0004000200080040000a0000007bffff00030001
40090303020201010000018db801ff85456844456844456844456844b305
0446002bb3070646002bb10404456844b1060645684400>
[16377 16225 1429 ] AllocGlyphStorage
]def 
108 44 
PrepFor2015
AddFontInfoBegin
AddFontInfo
/OrigFontType /TrueType def 
/OrigFontName <50726F647563747573426F6F6B> def
/OrigFontStyle () def
/FSType 1 def
AddFontInfoEnd
[16#2c 16#dac9c980 ] AddXUID
Type42DictEnd
1 7404 50 <0002002afff6026e02a4000f001f0047401901202040210c14040c1c0404
10060018060808020000010446762f3718003f3f10fd10fd012ffd2ffd00
3130014968b900040020496861b0405258381137b90020ffc03859052227
263534373633321716151407062732373635342726232207061514171601
4c814f524d5085814f524d50855f3c3f383a685f3c3f383a0a595da5935e
62565a9f986166434a4e8079494d474a7a7e4d51>/TTE152A528t00 AddT42Char 
1 8142 54 <00010022fff601ae02a40023004f401d012424402520160302080415201b
041005060018061313020000010246762f3718003f3f10fd10fd012ffd2f
3cfd2e002e2e3130014968b900020024496861b0405258381137b90024ff
c038591722273716333236353427262f012e013534363332170726232206
15141f011615140706dd6754264f463b4e191d460e5d546e5e6551264a46
394b790fb3373a0a3a3d3340362e1c1f14041a594850683a3d323c335a20
04308e523537>/TTE152A528t00 AddT42Char 
1 6176 42 <0001002afff6022502a4001f005640200120204021011101001e1d100403
010017040a13060e1b06060e020600010a46762f3718003f3f10fd10fd01
2ffd2e2e2e2e2e2e2e002e2e2e3130014968b9000a0020496861b0405258
381137b90020ffc0385901330f0115062322272635343736333217072623
22070615141716333237352701d2530402616d844f5451548b61541f4655
623f413a3d6b443f02013c3e3e9832575ca29560642a4127484c7d7b4b4f
1d693e00>/TTE152A528t00 AddT42Char 
1 11526 76 <0002002c0000009802cb00090015004e401b011616401713130d08060503
01000a061010060500010001010d46762f3718003f3c3f3c2f10fd012e2e
2e2e2e2e2e2e003130014968b9000d0016496861b0405258381137b90016
ffc0385913330f011317233f01033722263534363332161514063a500402
02045004020224171f2016171f1f01d93e3efee13e3e3e011fc41f17171f
1f171620>/TTE152A528t00 AddT42Char 

1 0 3 <> /TTE152A528t00 AddT42Char 
1 10692 72 <00020022fff4019901e50016001f005a402301202040210c1817140e0d15
0e04041704041a040d0c1d060812060008010000010446762f3718003f3f
10fd10fd012f3cfd2ffd10fd2e002e2e2e2e2e3130014968b90004002049
6861b0405258381137b90020ffc038591722272635343736333217161d01
21141716333237170603333f013426232206e3553537373552523235fed0
232438443c264ed4623e3e3d312d410c4044757145423b3e6d24442e3129
3c31012a02043c434e00>/TTE152A528t00 AddT42Char 
1 13044 85 <000100220000011c01e30012004e401b01131340140b0c0712100b050302
000e0606090601120000010546762f3718003f3c3f3c10fd012e2e2e2e2e
2e2e002e2e3130014968b900050013496861b0405258381137b90013ffc0
3859333f01352f01371736333217072623220713173a04020c124b0e284b
1b1319181b381402043e3ed73e3e1442420b4e0c34fedc3e>/TTE152A528t00 AddT42Char 
1 13720 89 <0001000c0000018701e1000f003d401201101040110c080c030b04010f00
00010346762f3718003f3c3f3c012e2e002e3130014968b9000300104968
61b0405258381137b90010ffc03859332f0103371f03371337170f0103a1
12146f4d1012420e0e54104a1715693e3e014f163e3ed33e3e01113e163e
3efeb100>/TTE152A528t00 AddT42Char 
1 10338 70 <00010022fff4017b01e50018004a401a011919401a0a160b170a1004040d
060814060008010000010446762f3718003f3f10fd10fd012ffd2e2e002e
2e3130014968b900040019496861b0405258381137b90019ffc038591722
2726353437363332170726232206151417163332371706e657353836385c
4f401f383e364722233e3c341f470c3f42756d4549273f225f5053323524
3f29>/TTE152A528t00 AddT42Char 
1 7574 51 <0002003a0000018b029a0010001a005a4022011b1b401c070d1a11100e0d
0302001604071a19060313060b040302100000010046762f3718003f3c3f
3c2ffd10fd3c012ffd2e2e2e2e2e2e2e2e002e3130014968b90000001b49
6861b0405258381137b9001bffc03859333f010333321615140706232227
151f0103163332363534262b013a04020689596f393c640f1c0204062011
3c4e453e383e3e021e62595c3a3d04943e3e0154044a3f384500>/TTE152A528t00 AddT42Char 
1 11994 79 <0001003a0000008a02da0007004340150108084009040706040302000403
03070000010046762f3718003f3c3f3c012e2e2e2e2e2e003130014968b9
00000008496861b0405258381137b90008ffc03859333f0103330f01133a
040206500402063e3e025e3e3efda200>/TTE152A528t00 AddT42Char 
1 9956 68 <00020026fff6017d01e30019002300624028012424402516180d160e1d1c
0803070514132104031e1d0607060b06101a06001001170000010346762f
3718003f3c3f10fd10fd2f3cfd3c012ffd2f3cfd173c2e2e002e2e313001
4968b900030024496861b0405258381137b90024ffc03859172226353436
3b0135342623220727363332161d011f0107270627323735232206151416
aa3a4a654c4431273e361f485242530c124b0e2b494823482c3a260a4a42
4552262d33223e284c43ce3e3e144242443d612d25242800>/TTE152A528t00 AddT42Char 
1 13372 87 <0001000efff60122026c0015006d403001161640171413140d0c0805040f
0e0b09040a0407060303020e0d040303060511060008070c0b0603050100
00010446762f3718003f3f173c2f3c10fd10fd173c012f173cfd173c2e2e
2e2e2e2e002e3130014968b900040016496861b0405258381137b90016ff
c038591722351123353335330f011533152311143332371706c1694a4a4b
04026b6b2a211c1e2d0a72012f42933e3e1742fed634133a1c00>/TTE152A528t00 AddT42Char 
1 10882 73 <0001000f0000017502e40018006c402d011919401a0c0d1816151413120c
07060504030200161504030306050f060a0a031413060305011800000104
46762f3718003f3c3f173c3f10fd10fd173c012e2e2e2e2e2e2e2e2e2e2e
2e2e2e002e3130014968b900040019496861b0405258381137b90019ffc0
3859333f011123353335343633321707262322061d013315231317520402
49495a4f42322427302c32565602043e3e011b42405f6c253c1c473f4042
fea73e00>/TTE152A528t00 AddT42Char 
1 12504 82 <00020022fff401b801e5000e001b00474019011c1c401d0b12040b180404
1506070f060007010000010446762f3718003f3f10fd10fd012ffd2ffd00
3130014968b90004001c496861b0405258381137b9001cffc03859172227
26353436333217161514070627323635342623220615141716ed5b363a6e
5d5a373a36385d394b453f394b22230c4043776b8c3d417170474b426454
51645e4f55343700>/TTE152A528t00 AddT42Char 
1 12100 80 <000100220000029a01e300290068402a012a2a402b13251a07292722211f
1e1c17161413050302000b06060d090601291f1e1413050000010546762f
3718003f173c3f3c3c10fd012e2e2e2e2e2e2e2e2e2e2e2e2e2e2e002e2e
2e3130014968b90005002a496861b0405258381137b9002affc03859333f
01352f013717363332173633321716151317233f01353426232207131723
3f0135342623220713173a04020c124b0e3555512539593c242702045004
023127472502045004023127472502043e3ed73e3e1442424646282b4afe
f83e3e3eb732383bfedc3e3e3eb732383bfedc3e>/TTE152A528t00 AddT42Char 
1 7944 53 <0002003afff001b6029a0014001f0061402601202040210c1f151412110c
0a0302001a04071f1e0603170611100d00040302140000010046762f3718
003f3c3f3c3f2f3cfd10fd3c012ffd2e2e2e2e2e2e2e2e2e2e0031300149
68b900000020496861b0405258381137b90020ffc03859333f0103333216
151406071f01072f0223151f010316333236353427262b013a0402068957
6d403780264b1e206244020406151c3a4c20233c383e3e021e5b533f6612
e63e213e3eb4a43e3e016804473b321e2000>/TTE152A528t00 AddT42Char 
1 13188 86 <00010020fff6014101e30022004f401d012323402414150302080520141a
050f17061205060012010000010246762f3718003f3f10fd10fd012ffd2f
3cfd2e002e2e3130014968b900020023496861b0405258381137b90023ff
c038591722273716333236353427262f0126353436333217072623220615
14161f0116151406a3453e1f353b222d1013301175574a44371f2e3b232e
23301175560a263f232a231c10130b041c693c4f233d1e26201c220b041c
6a3f5300>/TTE152A528t00 AddT42Char 
1 3214 23 <00020018ff6601bd01e900130016006b402b01171740180e150e16151413
11100f0e0c0b09050403020016140d030c06100f04030313000908010104
46762f3718003f3c2f3c2f173cfd173c012e2e2e2e2e2e2e2e2e2e2e2e2e
2e2e2e002e2e3130014968b900040017496861b0405258381137b90017ff
c03859053f013523353f0113330f01113f011523151f01033507010a0402
f82c2ab537040235346902044a9a9a3e3e442e3e3e01193e3efef9020446
443e3e0100efef00>/TTE152A528t00 AddT42Char 
1 0 0 <0002003f000001b602ee0003000700564020010808400902070405010006
0505030205040600070606010201030000010046762f3718003f3c2f3c10
fd3c10fd3c012f3cfd3c2f3cfd3c003130014968b900000008496861b040
5258381137b90008ffc0385933112111253311233f0177fec7fafa02eefd
123f0271>/TTE152A528t00 AddT42Char 
/TTE152A528t00 findfont /CharStrings get begin
/O 50 def
/S 54 def
/G 42 def
/i 76 def
/space 3 def
/e 72 def
/r 85 def
/v 89 def
/c 70 def
/P 51 def
/l 79 def
/a 68 def
/t 87 def
/f 73 def
/o 82 def
/m 80 def
/R 53 def
/s 86 def
/four 23 def
/.notdef 0 def
end
/TTE152A528t00 findfont /Encoding get
dup 1 /O put
dup 2 /S put
dup 3 /G put
dup 4 /i put
dup 5 /space put
dup 6 /e put
dup 7 /r put
dup 8 /v put
dup 9 /c put
dup 10 /P put
dup 11 /l put
dup 12 /a put
dup 13 /t put
dup 14 /f put
dup 15 /o put
dup 16 /m put
dup 17 /R put
dup 18 /s put
dup 19 /four put
pop
Pscript_WinNT_Incr begin
%%BeginResource: file Pscript_Text 5.0 0
/TextInit{TextInitialised? not{/Pscript_Windows_Font & d/TextInitialised? T d
/fM[1 0 0 1 0 0]d/mFM matrix d/iMat[1 0 0.212557 1 0 0]d}if}b/copyfont{1 ^
length add dict `{1 ^/FID ne{d}{! !}?}forall & E}b/EncodeDict 11 dict d/bullets
{{/bullet}repeat}b/rF{3 copyfont @ ` ~ EncodeDict ~ get/Encoding ~ 3 ^/0 eq{&
/CharStrings known{CharStrings/Eth known not{! EncodeDict/ANSIEncodingOld get}
if}if}if d E}b/mF{@ 7 1 $ findfont ~{@/Encoding get @ StandardEncoding eq{! T}{
{ISOLatin1Encoding}stopped{! F}{eq}?{T}{@ ` T 32 1 127{Encoding 1 ^ get
StandardEncoding 3 -1 $ get eq and}for E}?}?}{F}?{1 ^ ~ rF}{0 copyfont}? 6 -2 $
! ! ~ !/pd_charset @ where{~ get 128 eq{@ FDV 2 copy get @ length array copy
put pd_CoverFCRange}if}{!}? 2 ^ ~ definefont fM 5 4 -1 $ put fM 4 0 put fM
makefont Pscript_Windows_Font 3 1 $ put}b/sLT{: Lw -M currentpoint snap M 0 - 0
Lc K ;}b/xUP null d/yUP null d/uW null d/xSP null d/ySP null d/sW null d/sSU{N
/uW ~ d/yUP ~ d/xUP ~ d}b/sU{xUP yUP uW sLT}b/sST{N/sW ~ d/ySP ~ d/xSP ~ d}b/sT
{xSP ySP sW sLT}b/sR{: + R 0 0 M}b/sRxy{: matrix astore concat 0 0 M}b/eR/; , d
/AddOrigFP{{&/FontInfo known{&/FontInfo get length 6 add}{6}? dict `
/WinPitchAndFamily ~ d/WinCharSet ~ d/OrigFontType ~ d/OrigFontStyle ~ d
/OrigFontName ~ d & E/FontInfo ~ d}{! ! ! ! !}?}b/mFS{makefont
Pscript_Windows_Font 3 1 $ put}b/mF42D{0 copyfont `/FontName ~ d 2 copy ~ sub 1
add dict `/.notdef 0 d 2 copy 1 ~{@ 3 ^ sub Encoding ~ get ~ d}for & E
/CharStrings ~ d ! ! & @ E/FontName get ~ definefont}b/mF42{15 dict ` @ 4 1 $
FontName ~ d/FontType 0 d/FMapType 2 d/FontMatrix[1 0 0 1 0 0]d 1 ^ 254 add 255
idiv @ array/Encoding ~ d 0 1 3 -1 $ 1 sub{@ Encoding 3 1 $ put}for/FDepVector
Encoding length array d/CharStrings 2 dict `/.notdef 0 d & E d 0 1 Encoding
length 1 sub{@ @ 10 lt{! FontName length 1 add string}{100 lt{FontName length 2
add string}{FontName length 3 add string}?}? @ 0 FontName @ length string cvs
putinterval @ 3 -1 $ @ 4 1 $ 3 string cvs FontName length ~ putinterval cvn 1 ^
256 mul @ 255 add 3 -1 $ 4 ^ findfont mF42D FDepVector 3 1 $ put}for & @ E
/FontName get ~ definefont ! ! ! mF}b/mF_OTF_V{~ ! ~ ! 4 -1 $ ! findfont 2 ^ ~
definefont fM @ @ 4 6 -1 $ neg put 5 0 put 90 matrix R matrix concatmatrix
makefont Pscript_Windows_Font 3 1 $ put}b/mF_TTF_V{3{~ !}repeat 3 -1 $ !
findfont 1 ^ ~ definefont Pscript_Windows_Font 3 1 $ put}b/UmF{L2?
{Pscript_Windows_Font ~ undef}{!}?}b/UmF42{@ findfont/FDepVector get{/FontName
get undefinefont}forall undefinefont}b
%%EndResource
end reinitialize
F /F0 0 /0 F /TTE152A528t00 mF 
/F0S41 F0 [65 0 0 -65 0 0 ] mFS
F0S41 Ji 
819 5068 M <010203040502060708040906050A0B0C0D0E0F07100511060B060C12060513>[47 33 43 16 16 34 32 22 30 16 28 32 17 30 16 31 23 20 34 22 50 17 32 33 16 32 30 27 32 16  0]xS 
951 VM?
1 3402 24 <00010022ff5b017f01d9001a005e4024011b1b401c1812110f030f0e0213
12050d0c0804180c0b061413050600000e0d01010246762f3718003f3c2f
10fd2f3cfd3c012ffd2f3cfd3c2e2e2e002e2e2e2e3130014968b9000200
1b496861b0405258381137b9001bffc0385917222737163332363534262b
011121152f01231533321716151406b55241292e3c394a554d5401293e3e
6a15663e416ea5323926473d394701354c0402aa30335a5b7600>/TTE152A528t00 AddT42Char 
1 2256 16 <0001001e00bd0136010d0007004240140108084009000706030207000404
030400010346762f3718002f2f012f3cfd3c002e2e2e2e3130014968b900
030008496861b0405258381137b90008ffc03859252f0123351f01330136
3e3e9c3e3e9cbd04024a0402>/TTE152A528t00 AddT42Char 
1 2550 19 <00020028fff601c201e3000d001900474019011a1a401b0a17050311040a
1406060e060006010000010346762f3718003f3f10fd10fd012ffd2ffd00
3130014968b90003001a496861b0405258381137b9001affc03859172226
3534363332171615140706273236353426232206151416f55b726f5e5b38
3a36395e3b4d48403b4e480a82766a8b3d41716e474940645450655f5054
6a00>/TTE152A528t00 AddT42Char 
/TTE152A528t00 findfont /CharStrings get begin
/five 24 def
/hyphen 16 def
/zero 19 def
end
/TTE152A528t00 findfont /Encoding get
dup 20 /five put
dup 21 /hyphen put
dup 22 /zero put
pop
3241 5068 M <1415141616>[31 25 31 35  0]xS 
30000 VM?
Pscript_WinNT_Compat begin /$x mysetup def end
Pscript_WinNT_Incr dup /suspend get exec
Pscript_WinNT_Compat dup /initialize get exec
%%BeginDocument: Pscript_Win_PassThrough
813 5002/G1380133 FmND

%%EndDocument

Pscript_WinNT_Compat dup /suspend get exec
Pscript_WinNT_Incr dup /resume get exec
/DeviceRGB dup setcolorspace /colspABC exch def
0 0 0 1 scol 1904 VM?
1 6654 45 <0001000eff26008d029a000900404013010a0a400b010907040301000601
0002010746762f3718003f3c2f012e2e2e2e2e2e003130014968b9000700
0a496861b0405258381137b9000affc0385913330f011114072736353a53
0402423732029a3e3efde281591f5368>/TTE152A528t00 AddT42Char 
1 6548 44 <0001003a0000008d029a0007004340150108084009010705040301000504
00010002010046762f3718003f3c3f3c012e2e2e2e2e2e003130014968b9
00000008496861b0405258381137b90008ffc0385913330f0113233f013a
53040206530402029a3e3efde23e3e00>/TTE152A528t00 AddT42Char 
1 12330 81 <000100220000019601e3001a00564020011b1b401c0f071a181312100f05
0302001606060906011a100f030000010546762f3718003f173c3f3c10fd
012e2e2e2e2e2e2e2e2e2e002e3130014968b90005001b496861b0405258
381137b9001bffc03859333f01352f0137173633321716151317233f0135
342623220713173a04020c124b0e35573d25270204500402332847260204
3e3ed73e3e144242282b4afef83e3e3eb732383bfedc3e00>/TTE152A528t00 AddT42Char 
1 8632 57 <0001000e000001f502a4000c003d4012010d0d400e090609030804020c00
00010346762f3718003f3c3f3c012e2e002e3130014968b90003000d4968
61b0405258381137b9000dffc03859332f01033717133713170f0103d710
12a74e10990c9c4815139d3e3e0210183efde23e021e143e3efdec00>/TTE152A528t00 AddT42Char 
1 2702 20 <000100160000016d01e800100056402001111140120f05040a060100040c
100f04030d0b0c0504030901100000010646762f3718003f3c3f012f3cfd
3c3c10fd3c10fd3c2e2e002e2e3130014968b900060011496861b0405258
381137b90011ffc0385933353f011107273f02170f01111f0115313e3e7c
1b4449421204023e3e400204013d3a421c2120123e3efeec04024000>/TTE152A528t00 AddT42Char 
1 2358 17 <00010028fff300a0006b000b0037400f010c0c400d090904030600000103
46762f3718003f2f012ffd003130014968b90003000c496861b040525838
1137b9000cffc0385917222635343633321615140664192323191a22230d
23191923221a1923>/TTE152A528t00 AddT42Char 
/TTE152A528t00 findfont /CharStrings get begin
/J 45 def
/I 44 def
/n 81 def
/V 57 def
/one 20 def
/period 17 def
end
/TTE152A528t00 findfont /Encoding get
dup 23 /J put
dup 24 /I put
dup 25 /n put
dup 26 /V put
dup 27 /one put
dup 28 /period put
pop
/F0S48 F0 [72 0 0 -72 0 0 ] mFS
F0S48 Ji 
819 550 M <1702110518190D06070C090D040F191205051A060712040F19051B1C16>[18 37 37 18 18 37 26 35 25 34 32 25 18 38 37 30 18 18 37 36 24 29 18 38 38 18 32 18  0]xS 
635 VM?
1 10490 71 <00020022fff601ad02da00160021005f4025012222402313150a1a191311
100e0d0b0a1f04041c06081706000e0d030801140000010446762f371800
3f3c3f3f3c10fd10fd012ffd2e2e2e2e2e2e2e2e2e002e2e3130014968b9
00040022496861b0405258381137b90022ffc03859172227263534373633
3217352f01330f01111f010727062732371126232206151416dc5233353b
3857342b02045004020c124b0e2a52482b2936384b3e0a3d417074474414
8f3e3e3e3efe283e3e144242463b010b1d6251535d00>/TTE152A528t00 AddT42Char 
1 13548 88 <00010037fff601ab01d9001900564020011a1a401b161816141311100e09
0806050c0600111006030501170000010546762f3718003f3c3f173c10fd
012e2e2e2e2e2e2e2e2e2e002e3130014968b90005001a496861b0405258
381137b9001affc03859172226350327330f011514163332370327330f01
151f01072706c63d4c02045004023328472602045004020c124b0e350a53
4a01083e3e3eb732383b01243e3e3ed73e3e14424200>/TTE152A528t00 AddT42Char 
/TTE152A528t00 findfont /CharStrings get begin
/d 71 def
/u 88 def
end
/TTE152A528t00 findfont /Encoding get
dup 29 /d put
dup 30 /u put
pop
3032 550 M <18190D070F1D1E090D040F19>[18 37 25 24 38 37 37 31 25 18 38  0]xS 
30000 VM?
Pscript_WinNT_Compat begin /$x mysetup def end
Pscript_WinNT_Incr dup /suspend get exec
Pscript_WinNT_Compat dup /initialize get exec
%%BeginDocument: Pscript_Win_PassThrough
813 478/G1380143 FmND

%%EndDocument

Pscript_WinNT_Compat dup /suspend get exec
Pscript_WinNT_Incr dup /resume get exec
Pscript_WinNT_Incr begin
%%BeginResource: file Pscript_Win_GdiObject 5.0 0
/SavedCTM null d/CTMsave{/SavedCTM SavedCTM currentmatrix d}b/CTMrestore
{SavedCTM setmatrix}b/mp null d/ADO_mxRot null d/GDIHMatrix null d
/GDIHPatternDict 22 dict d GDIHPatternDict `/PatternType 1 d/PaintType 2 d/Reps
L2?{1}{5}? d/XStep 8 Reps mul d/YStep XStep d/BBox[0 0 XStep YStep]d/TilingType
1 d/PaintProc{` 1 Lw[]0 sd PaintData , exec E}b/FGnd null d/BGnd null d
/HS_Horizontal{horiz}b/HS_Vertical{vert}b/HS_FDiagonal{fdiag}b/HS_BDiagonal
{biag}b/HS_Cross{horiz vert}b/HS_DiagCross{fdiag biag}b/MaxXYStep XStep YStep
gt{XStep}{YStep}? d/horiz{Reps{0 4 M XStep 0 - 0 8 +}repeat 0 -8 Reps mul + K}b
/vert{Reps{4 0 M 0 YStep - 8 0 +}repeat 0 -8 Reps mul + K}b/biag{Reps{0 0 M
MaxXYStep @ - 0 YStep neg M MaxXYStep @ - 0 8 +}repeat 0 -8 Reps mul + 0 YStep
M 8 8 - K}b/fdiag{Reps{0 0 M MaxXYStep @ neg - 0 YStep M MaxXYStep @ neg - 0 8
+}repeat 0 -8 Reps mul + MaxXYStep @ M 8 -8 - K}b E/makehatch{4 -2 $/yOrg ~ d
/xOrg ~ d GDIHPatternDict/PaintData 3 -1 $ put CTMsave GDIHMatrix setmatrix
GDIHPatternDict matrix xOrg yOrg + mp CTMrestore ~ U ~ 2 ^ put}b/h0{/h0
/HS_Horizontal makehatch}b/h1{/h1/HS_Vertical makehatch}b/h2{/h2/HS_FDiagonal
makehatch}b/h3{/h3/HS_BDiagonal makehatch}b/h4{/h4/HS_Cross makehatch}b/h5{/h5
/HS_DiagCross makehatch}b/GDIBWPatternMx null d/pfprep{save 8 1 $
/PatternOfTheDay 8 1 $ GDIBWPatternDict `/yOrg ~ d/xOrg ~ d/PaintData ~ d/yExt
~ d/Width ~ d/BGnd ~ d/FGnd ~ d/Height yExt RepsV mul d/mx[Width 0 0 Height 0
0]d E build_pattern ~ !}b/pfbf{/fEOFill ~ d pfprep hbf fEOFill{O}{L}? restore}b
/GraphInit{GDIHMatrix null eq{/SavedCTM matrix d : ADO_mxRot concat 0 0 snap +
: 0.48 @ GDIHPatternDict ` YStep mul ~ XStep mul ~ nonzero_dsnap YStep V ~
XStep V ~ E +S/GDIHMatrix matrix currentmatrix readonly d ; : 0.24 -0.24 +S
GDIBWPatternDict ` Width Height E nonzero_dsnap +S/GDIBWPatternMx matrix
currentmatrix readonly d ; ;}if}b
%%EndResource
%%BeginResource: file Pscript_Win_GdiObject_L3 5.0 0
/GDIBWPatternDict 25 dict @ `/PatternType 1 d/PaintType 1 d/RepsV 1 d/RepsH 1 d
/BBox[0 0 RepsH 1]d/TilingType 1 d/XStep 1 d/YStep 1 d/Height 8 RepsV mul d
/Width 8 d/mx[Width 0 0 Height neg 0 Height]d/FGnd null d/BGnd null d
/SetBGndFGnd{BGnd null ne{BGnd aload ! scol BBox aload ! 2 ^ sub ~ 3 ^ sub ~
rf}if FGnd null ne{FGnd aload ! scol}if}b/PaintProc{` SetBGndFGnd RepsH{Width
Height F mx PaintData imagemask Width 0 +}repeat E}b E d/mp/makepattern , d
/build_pattern{CTMsave GDIBWPatternMx setmatrix/nupangle where{! nupangle -90
eq{nupangle R}if}if GDIBWPatternDict @ ` Width Height ne{Width Height gt{Width
Height V 1}{1 Height Width V}? +S}if xOrg yOrg E matrix + mp CTMrestore}b/hbf
{setpattern}b/hf{:/fEOFill ~ d ~ ! setpattern fEOFill{O}{L}? ;}b/pbf{: !
/fEOFill ~ d GDIBWPatternDict `/yOrg ~ d/xOrg ~ d/PaintData ~ d/OutputBPP ~ d
/Height ~ d/Width ~ d/PaintType 1 d/PatternType 1 d/TilingType 1 d/BBox[0 0
Width Height]d/XStep Width d/YStep Height d/mx xOrg yOrg matrix + d 20 dict @ `
/ImageType 1 d/Width Width d/Height Height d/ImageMatrix[1 0 0 1 0 0]d
/BitsPerComponent 8 d OutputBPP 24 eq{/Decode[0 1 0 1 0 1]d}{OutputBPP 8 eq{
/Decode[0 1]d}{/Decode[0 1 0 1 0 1 0 1]d}?}?/DataSource{PaintData}d E/ImageDict
~ d/PaintProc{` ImageDict image E}b & mx makepattern setpattern E fEOFill{O}{L}
? ;}b/mask_pbf{:/fEOFill ~ d 20 dict `/yOrg ~ d/xOrg ~ d/PaintData ~ d/Height ~
d/Width ~ d/PatternType 1 d/PaintType 2 d/TilingType 1 d/BBox[0 0 Width Height]
d/XStep Width d/YStep Height d/mx xOrg yOrg matrix + d/PaintProc{` Width Height
T 1 1 dtransform abs ~ abs ~ 0 0 3 -1 $ 0 0 6 array astore{PaintData}imagemask
E}b & mx makepattern setpattern E fEOFill{O}{L}? ;}b/grf4{4 dict `/ShadingType
4 d/DataSource ~ d/ColorSpace ~ d & E shfill}b
%%EndResource
end reinitialize
N 3400 613 M 3402 613 I 3402 609 I 3400 609 I C 
/DeviceRGB dup setcolorspace /colspABC exch def
0 0 0 1 scol  L N 1344 613 M 1342 613 I 1342 609 I 1344 609 I C 
 L N 3400 613 M 3400 609 I 1344 609 I 1344 613 I C 
 L N 1300 613 M 1302 613 I 1302 609 I 1300 609 I C 
 L N 819 613 M 817 613 I 817 609 I 819 609 I C 
 L N 1300 613 M 1300 609 I 819 609 I 819 613 I C 
 L N 3400 4989 M 3402 4989 I 3402 4985 I 3400 4985 I C 
 L N 1344 4989 M 1342 4989 I 1342 4985 I 1344 4985 I C 
 L N 3400 4989 M 3400 4985 I 1344 4985 I 1344 4989 I C 
 L N 1300 4989 M 1302 4989 I 1302 4985 I 1300 4985 I C 
 L N 819 4989 M 817 4989 I 817 4985 I 819 4985 I C 
 L N 1300 4989 M 1300 4985 I 819 4985 I 819 4989 I C 
 L ; /DeviceRGB dup setcolorspace /colspABC exch def
0 0 0 1 scol 5694 VM?
-0.07 -0.297 1.223 0.957
 256 array 0 1 255 {1 index exch /.notdef put} for  /TTE16B5380t00
Type42DictBegin
[<00010000000a000a000a000a6376742059def980000000ac0000002a6670
676d8333c24f000000d800000014676c796627f32c9d0000095800008344
68656164d28ddcc4000000ec0000003668686561083f0448000001240000
0024686d7478c3a918a200000148000003dc6c6f6361003ecb8400000524
000003e06d61787001790233000009040000002070726570c503cc0e0000
09240000003467646972000000000000000000000000fff501e602a402d6
007a0073007300685a671206d2b86a18f82a61a30e40eed2803a2755a287
0001000d00004001002c764520b003254523616818236860442d00010000
00010000ff542e805f0f3cf5000003e800000000b77dcca800000000b77d
cca8ffb6fed504c803be0000000300020001000000000001000003befed5
000004e6ffb6ff9504c80001000000000000000000000000000000f701f4
003f0000000000c8000000c8000000f7002f016000250254002101f40022
036d001e0295002900b500250153004201530017020f002201ff002000e5
00150175001e00e50026015c001201ef002301a0001501d3002401c40018
01f4001601d3001f01f1002901c7002801e6002601f1002600e5002600e5
00130190002001d700200190002001c7001c03ec00250241000b01e60031
023a00230270003101b90031019c0031026a00230273003100e7003100e7
00050218003101980031031400310268003102a5002301d7003102a50023
01f6003101e3001b01ff000e0252002b0240000b0370000b02220007022a
000c01fd0023015f0039013d0011015f001c012cfff5018afff8012cffdd
01bd001f01e9002e0198001f01ec001e01d3001f0124000c01c6001601e6
002e00de002500de000c01c6002e00de002e02eb001901e5001901ec001f
01e9001901e7001e01440019017100170155000d01e5002a01c8000802f1
000801ba000c01c8000801a5001b01b1001400d6002e01b1001601800016
0241000b0241000b023a002301b900310268003102a500230252002b01bd
001f01bd001f01bd001f01bd001f01bd001f01bd001f0198001f01d3001f
01d3001f01d3001f01d3001f00de002e00deffb600deffce00deffbe01e5
001901ec001f01ec001f01ec001f01ec001f01ec001f01e5002a01e5002a
01e5002a01e5002a01e70021014e0018019d001f01f40022017300170168
003c02240011022e002e02ed002c02ed002c02fa001b012c005a012cffe5
02f6000a02a5002302f9000c022b000901e5002a015b0025015b001402bc
001f01ec001f0177001f00f7002f01a5002101f4ffdd0267001d0267001e
0296002600c800010241000b0241000b02a50023032800230305001f01f4
001e03e8001e01a5002601a5001500e4002600e5001501ff002001c80008
022a000c025200140160001d0160001e01e7002100e5002600e5001501a5
001504e6001e0241000b01b900310241000b01b9002b01b9002300e70031
00e7ffd200e7ffc200e7ffba02a5002302a5002302a500230252002b0252
002b0252002b012cfff5012cffec012cfff1012c002a01e3001b01710017
00d700320270ffef01ec001e022a000c01c8000801d7003301e9003001ff
0020018c003c018c0034018c003b018c002f018c0019018c001900000001
0198fff400deffe701fd002301a5001b0227000c02f9000c019000200190
00250300000c01f6000c02fe000c02ed002c01fdffe6035d002d01df0005
0180001602de000c0250000c015c001201f6000c01f6000c03e8000000de
002e012cfff2012c004a012c0018012cffc7012c0045012cfff500000000
0000007c0000007c0000007c0000007c0000010600000188000002a00000
03800000048000000578000005d600000646000006b60000078a00000820
00000898000008fe00000960000009be00000a5000000adc00000b7c0000
0c4400000d0000000dac00000e5a00000ed400000faa0000105a00001072
0000108a000010e60000117c000011d80000129c000013cc000014600000
1538000015c8000016680000171c000017b80000186a0000192600001990
000019fa00001aa200001b2400001be600001c8400001d2c00001dd80000
1e8c00001f4e000020000000208a0000212e000021aa00002256000022f4
0000238600002424000024ae0000250c0000259c000025ac000026020000
265600002720000027d2000028640000292a000029d800002a8c00002ba2
00002c5000002cea00002d8000002e2800002e9200002f70000030180000
30a4000031680000321e000032b20000335a00003410000034ba00003536
000035e80000368a00003716000037b200003870000038d8000039940000
39a6000039be00003a9600003b6800003b8000003b9800003bb000003bc8
00003bde00003bf400003c0a00003c2000003c3600003d4000003e0a0000
3e2000003e3600003e4c00003e6200003e7800003e8e00003ea400003eba
00003ed000003ee600003efc00003f1200003f2800003f3e00003f540000
3f6a00003f8000003f9600004048000040d600004198000042640000434c
000043ae000044520000453e00004656000047380000484e000048a40000
492e00004a1800004b0200004c2c00004d3200004df000004eac00004f36
0000505c00005138000051fc00005284000052f4000053b8000054320000
54ac000054cc000054cc000054e4000054fc000055140000560c000056fc
00005764000057cc0000588000005936000059b0000059c200005a860000
5a9c00005ab400005bc000005c1a00005c7400005d6c00005d7e00005d8e
00005e4000005f8a00005fa200005fba00005fd200005fea000060020000
601a000060320000604a000060620000607a00006092000060aa000060c2
000060da000060f20000614c000061d40000623c000062ba000062d20000
62e80000636c0000643c000065220000653a0000655000006606000066ce
00006724000067b6000068520000691000006a1a00006b5000006cb00000
6cb000006d6000006dfc00006e1400006e2a00006f3800007066000070ee
00007176000072960000737000007488000075920000761c000079440000
79c2000079dc00007af000007bfc00007c0c00007ce200007db600008088
000080f80000816c0000817e0000820600008278000082ea000083440001
000000f700e20007003b0004000200080040000a00000061ffff00030001
40090303020201010000018db801ff85456844456844456844456844b305
0446002bb3070646002bb10404456844b1060645684400>
[16257 15501 1849 ] AllocGlyphStorage
]def 
108 44 
PrepFor2015
AddFontInfoBegin
AddFontInfo
/OrigFontType /TrueType def 
/OrigFontName <50726F64756374757353656D69626F6C64> def
/OrigFontStyle () def
/FSType 1 def
AddFontInfoEnd
[16#2c 16#6a834d9 ] AddXUID
Type42DictEnd
1 2780 21 <00010024000001a301e400160058402101171740181515091615120a0100
040510141312070007060c0c01160000010a46762f3718003f3c3f10fd10
fd3c3c012ffd2e2e2e2e2e2e002e2e3130014968b9000a0017496861b040
5258381137b90017ffc0385933353e013534262322072736333217161514
07333f01153367842a2346392e5564513133c0553e3e7e23632c1d252763
36282b4a865902046e00>/TTE16B5380t00 AddT42Char 
1 0 0 <0002003f000001b602ee0003000700564020010808400902070405010006
0505030205040700070607010201030000010046762f3718003f3c2f3c10
fd3c10fd3c012f3cfd3c2f3cfd3c003130014968b900000008496861b040
5258381137b90008ffc0385933112111253311233f0177fec7fafa02eefd
123f0271>/TTE16B5380t00 AddT42Char 
/TTE16B5380t00 findfont /CharStrings get begin
/two 21 def
/.notdef 0 def
end
/TTE16B5380t00 findfont /Encoding get
dup 1 /two put
pop
F /F1 0 /0 F /TTE16B5380t00 mF 
/F1SCC F1 [204 0 0 -204 0 0 ] mFS
F1SCC Ji 
819 835 M <01>S 
4379 VM?
1 6544 45 <00010005ff1800b5029a000900404013010a0a400b010907040301000601
0002010746762f3718003f3c2f012e2e2e2e2e2e003130014968b9000700
0a496861b0405258381137b9000affc0385913330f011114072736353184
0402486232029a3e3efde27c6c2f5b5e>/TTE16B5380t00 AddT42Char 
1 8014 54 <0001001bfff501ca02a50020004f401d0121214022131403130208041e19
040d05060016061111020000010246762f3718003f3f10fd10fd012ffd2f
fd2e2e002e2e3130014968b900020021496861b0405258381137b90021ff
c03859172227371633323635342f01263534373633321707262322061514
1f0116151406de695a324945323a6411ac413e69605c334142333c5e11b2
7f0b316e282b253f1804289d5e3734326e2829234116042a98616e00>/TTE16B5380t00 AddT42Char 
1 7820 53 <00020031ffeb01f1029a0014001e00614026011f1f40200c1e151412110c
0a0302001a0408170711101e1d07030d00040302140000010046762f3718
003f3c3f3c3f10fd3c2f3cfd012ffd2e2e2e2e2e2e2e2e2e2e0031300149
68b90000001f496861b0405258381137b9001fffc03859333f0103333217
161514071f01072f0223151f0103163332363534262b0131040206bb5e39
3d6b75277d1d1f5633020406151d2a38332d343e3e021e2f33578e2cd03e
2e3e3ea08b3e3e017805342d2730>/TTE16B5380t00 AddT42Char 

1 0 3 <> /TTE16B5380t00 AddT42Char 
1 6438 44 <00010031000000b5029a0007004340150108084009010705040301000504
00010002010046762f3718003f3c3f3c012e2e2e2e2e2e003130014968b9
00000008496861b0405258381137b90008ffc0385913330f0113233f0131
84040206840402029a3e3efde23e3e00>/TTE16B5380t00 AddT42Char 
1 12144 81 <00010019000001bb01e7001800574021011919401a0e07181612110f0e05
03020014070909010601180f0e030000010546762f3718003f173c3f3f10
fd012e2e2e2e2e2e2e2e2e2e002e3130014968b900050019496861b04052
58381137b90019ffc03859333f01352f01371736333216151317233f0135
3423220713173004020c117d0b36554049020482040246331b02043e3ed5
3e3e1a4541524cfef93e3e3ea5542afef33e>/TTE16B5380t00 AddT42Char 
1 13146 87 <0001000dfff5014d026c00160072403301171740181514150e0d09060510
0f0c0a040b0403000408070403030f0e050304070612070009080d0c0703
06010000010546762f3718003f3f173c2f3c10fd10fd173c012f173cfd10
fd173c2e2e2e2e2e2e002e3130014968b900050017496861b04052583811
37b90017ffc038591722263d0123353335330f0115331523151433323717
06ce3a4047477c040268682d1a1c203d0b4942f267933e3e1767d7380f5d
2000>/TTE16B5380t00 AddT42Char 
1 10538 72 <0002001ffff501b601e40012001b00574021011c1c401d091413100b0a11
16040a09130b04031907060e070006010000010346762f3718003f3f10fd
10fd012ffd3c2f3cfd2e002e2e2e2e2e3130014968b90003001c496861b0
405258381137b9001cffc038591722263534363332161d01211416333237
170603333f013426232206ef5c74765b5d69fee3372d4744255bb7383635
2d23232e0b817474868278242b3c1f6326012c0204292f35>/TTE16B5380t00 AddT42Char 
1 12830 85 <000100190000014401e700130051401d01141440150b0c071311100b0503
02000e060909010601130000010546762f3718003f3c3f3f10fd012e2e2e
2e2e2e2e2e002e2e3130014968b900050014496861b0405258381137b900
14ffc03859333f01352f013717363332170726232207151f013004020c11
7d0b274a1e141e17212c1702043e3ed53e3e1a45410a851325c63e3e>/TTE16B5380t00 AddT42Char 
1 9814 68 <0002001ffff201a301e30019002200634029012323402416180d160e2105
041d1c0903080414131e1d0708071a07000b071017001001000001044676
2f3718003f3f3f10fd10fd2f3cfd3c012f3cfd173c2ffd2e2e002e2e3130
014968b900040023496861b0405258381137b90023ffc038591722272635
34363b01353423220727363332161d011f01072706273237352322061514
a33b232670562d4a3e2e3a54664c5f0c107d0b2a2a311a31222c0b242542
4b571441255a374f48c43e3e1a4542662a4c211d3800>/TTE16B5380t00 AddT42Char 
1 10194 70 <0001001ffff3018d01e50016004a401a011717401809140a15090f04040c
070712070007010000010446762f3718003f3f10fd10fd012ffd2e2e002e
2e3130014968b900040017496861b0405258381137b90017ffc038591722
272635343633321707262322061514163332371706fe633c407d644c412d
29372d3c3b32322b29420d3f4273728c22621c4d4143501d6125>/TTE16B5380t00 AddT42Char 
1 11344 76 <00020025000000b902d000090015004e401b011616401713130d08060503
01000a061010060500010001010d46762f3718003f3c3f3c2f10fd012e2e
2e2e2e2e2e2e003130014968b9000d0016496861b0405258381137b90016
ffc0385913330f011317233f01033722263534363332161514062e820402
0204820402023d1f2b2b1f202a2b01d93e3efee13e3e3e011fa12b1f1f2b
2a201f2b>/TTE16B5380t00 AddT42Char 
1 12312 82 <0002001ffff401cd01e5000b0015004740190116164017091404030f0409
1107060c070006010000010346762f3718003f3f10fd10fd012ffd2ffd00
3130014968b900030016496861b0405258381137b90016ffc03859172226
35343633321615140627323635342322061514f562747662637376602a33
5f2b330c83746f8b7f72718f694c429148409700>/TTE16B5380t00 AddT42Char 
1 12978 86 <00010017fff4015a01e4001d004f401d011e1e401f111203020705111b16
050c14070f0507000f010000010246762f3718003f3f10fd10fd012ffd2f
3cfd2e002e2e3130014968b90002001e496861b0405258381137b9001eff
c038591722273716333235342f01263534363332170726232215141f0116
151406b15149243a42333d11805c4d5045243442353d11805b0c225f2030
240e041e724257205e1d2c230e041e744458>/TTE16B5380t00 AddT42Char 
/TTE16B5380t00 findfont /CharStrings get begin
/J 45 def
/S 54 def
/R 53 def
/space 3 def
/I 44 def
/n 81 def
/t 87 def
/e 72 def
/r 85 def
/a 68 def
/c 70 def
/i 76 def
/o 82 def
/s 86 def
end
/TTE16B5380t00 findfont /Encoding get
dup 2 /J put
dup 3 /S put
dup 4 /R put
dup 5 /space put
dup 6 /I put
dup 7 /n put
dup 8 /t put
dup 9 /e put
dup 10 /r put
dup 11 /a put
dup 12 /c put
dup 13 /i put
dup 14 /o put
dup 15 /s put
pop
1344 835 M <02030405060708090A0B0C080D0E070F>[53 105 108 47 54 105 76 101 72 97 90 76 51 107 105  0]xS 
30000 VM?
Pscript_WinNT_Compat begin /$x mysetup def end
Pscript_WinNT_Incr dup /suspend get exec
Pscript_WinNT_Compat dup /initialize get exec
%%BeginDocument: Pscript_Win_PassThrough
813 631/G1212549 FmND

%%EndDocument

Pscript_WinNT_Compat dup /suspend get exec
Pscript_WinNT_Incr dup /resume get exec
/DeviceRGB dup setcolorspace /colspABC exch def
0 0 0 1 scol 9839 VM?
-0.141 -0.285 1.141 0.922
 256 array 0 1 255 {1 index exch /.notdef put} for  /TTE151EA88t00
Type42DictBegin
[<00010000000a000a000a000a63767420fafb5e18000000ac000000386670
676d8333c24f000000e400000014676c7966c50439ec00000a400000963a
68656164d1fdf32f000000f80000003668686561078203f8000001300000
0024686d7478f9c0112000000154000004386c6f63610051c6640000058c
0000043c6d61787001f602e1000009c80000002070726570d8b5c4950000
09e80000005767646972000000000000000000000000fff401e402a302de
004800410045003c003e008a00a5001d015101db01b75a671206d2b86a18
f82a61a30e40eed2803a2755a2870001000d4001002c764520b003254523
616818236860442d0001000000010000abcfc57e5f0f3cf5000003e80000
0000b781d7eb00000000b781d7ebff6ffedf047703990000000300020001
00000000000100000399fedf000004afff6fff5b04770001000000000000
0000000000000000010e01f4003f0000000000c8000000c8000000e7001c
012a005d0210001701b5000f034b004c0239003000a3005d012e004a012e
ffdf01fa000e01ed001e00c2ffee014f001500c200090127ffef01e80022
01970015019e000d01a9fff601c4fff201a1fff701c10022018d001801c8
002401c1001000c2000900c2ffee0165002301e200130165000201970047
03e4002301ebffea01b7001502280035025a001501970015018600150247
00330248001500b1001500b1ffc701be00150185001502e10015022b0015
02780033019500150278003301bc001501b6000f01fe004c021d003401ea
004402e200440201fff201e3004301f80004012ffffd012e004b012fffdf
012c003801a1ffb9012c002101cc001f01cb001a018e002001cc001f0196
001f00edff6f01d2fffd01c3001900b6001800b6ffc60183001900b70019
02cc001b01c5001b01c8001e01cdfffd01cc001f011a001b01510000012f
002b01c5002701870028025600280170fff001870028017efffa017c001d
00b3fff9017cffcd015f002601ebffea01ebffea0228003501970015022b
001502780033021d003401cc001f01cc001f01cc001f01cc001f01cc001f
01cc001f018e00200196001f0196001f0196001f0196001f00b6001800b6
ffe500b6fffc00b6000701c5001b01c8001e01c8001e01c8001e01c8001e
01c8001e01c5002701c5002701c5002701c5002701c3004301460057018d
002101f400260160fff9010f003401be003c01f5ff7202d1003602d10036
02cd0042012c0095012c004302c3ffeb027800330274ff6f01eb002c01c5
fff60177004f0177004e02c0001f01c8001e0150ffe300e7001101920025
01f4ffe30220001a02200003022d000900c8ffe001ebffea01ebffea0278
003302f5003202d4001e01f4001303e80013015d0058015d004600c20058
00c2004701ed00020187002801e300430255001c013f001a013f000301c3
001d00c2002400c2ffee015dffee04af004c01ebffea0197001501ebffea
019700150197001500b1001500b1000700b1001200b1fff0027800330278
003302780033021d0034021d0034021d0034012c0038012c0040012c003d
012cfffc01b6000f0151000000bbfff8025afffa01cc001f01e300430187
00280195001401cdfffc01ee001c03e800000199006c019900670199005d
019900190199fff40199fff403e8000003e8000003e8000003e8000003e8
000003e8000003e8000003e8000003e8000003e8000003e8000003e80000
03e8000003e8000003e8000003e8000003e8000003e8000003e8000003e8
000003e8000003e800000000ffdc0185fff900b7fffc01f80004017efffa
01c7ff6f0273ff6f0165fff00165ffe702afff6f019aff6f02a6ff6f02d1
003601efffdc037f004601b4002b0189002e0267ff6f020dff700127ffef
019aff6f019bff6f03e8ffb900b60018012c0047012c0094012c0058012c
0017012c0017012c0045000000000000007c0000007c0000007c0000007c
0000011a000001c2000003360000043e000005600000066c000006de0000
0744000007aa0000089000000948000009c000000a2800000a8a00000af4
00000b8e00000c8800000d2e00000dfe00000eee00000fb80000106a0000
110a000011f4000012a6000012be000012d600001348000013e400001440
0000150a0000163600001710000017de0000187200001902000019b40000
1a5600001b1400001bd800001c3e00001ca600001d6600001dde00001ec2
00001f8a00002026000020d20000219c0000227000002338000023de0000
24a200002548000026240000271a000027d60000289a000029260000298e
00002a1400002a2400002a7800002ace00002b9600002c4a00002cdc0000
2dcc00002e6c00002f8a0000307800003142000031e80000328e00003346
000033a8000034b20000357e00003612000036ee000037a20000383c0000
38f400003a0200003ae000003b7200003c7600003d7a00003e2600003efc
00003fec000040420000412c0000413e00004156000042680000433e0000
43560000436e000043860000439e000043b4000043ca000043e0000043f6
0000440c00004520000046040000461a00004630000046460000465c0000
4672000046880000469e000046b4000046ca000046e0000046f60000470c
00004722000047380000474e000047640000477a000047900000486e0000
48f4000049ce00004aa400004b9c00004bfe00004cb800004de000004f3c
000050380000518a000051de000052680000538c000054b8000056660000
57ac0000588400005940000059d200005ac800005bc400005c8c00005d2a
00005dac00005eae00005f2800005fa200005fc200005fc200005fda0000
5ff20000600a000061160000621800006282000062ec000063a400006460
000064dc000064ee000065d6000065ec000066040000670c000067660000
67c0000068f00000690200006912000069c400006b3c00006b5400006b6c
00006b8400006b9c00006bb400006bcc00006be400006bfc00006c140000
6c2c00006c4400006c5c00006c7400006c8c00006ca400006d0000006d86
00006df600006e8800006ea000006eb600006f5e00007054000071740000
718c000071a20000725400007314000073680000736800007432000074d6
0000759e000076fa000078b200007a6a00007a6a00007a6a00007a6a0000
7a6a00007a6a00007a6a00007a6a00007a6a00007a6a00007a6a00007a6a
00007a6a00007a6a00007a6a00007a6a00007a6a00007a6a00007a6a0000
7a6a00007a6a00007a6a00007a6a00007a6a00007b3400007be800007c00
00007c1600007dc800007f8e0000802c000080b60000824e0000837c0000
852800008664000086fc00008ae600008b7800008b9200008d1e00008e2e
00008e3e00008f6e000090a000009374000093f0000094680000947a0000
950600009578000095e00000963a00010000010e00e30007003600040002
00080040000a000000c7ffff0003000140170e0e0d0d0c0c0b0b0a0a0909
08080303020201010000018db801ff854568444568444568444568444568
44456844456844456844456844456844456844b3050446002bb307064600
2bb10404456844b106064568440000>
[16365 15837 6259 ] AllocGlyphStorage
]def 
108 44 
PrepFor2015
AddFontInfoBegin
AddFontInfo
/OrigFontType /TrueType def 
/OrigFontName <50726F6475637475734F534769426F6F6B634974616C6963> def
/OrigFontStyle () def
/FSType 1 def
AddFontInfoEnd
[16#2c 16#3185fe02 ] AddXUID
Type42DictEnd
1 9378 57 <000100440000021802a7000800784034010909400a070307010302030408
080800070708040508080800070708020102030803040101020000010602
0202080000010146762f3718003f3c3f3f01872e08c408fc08c4872e08c4
0efc0ec4872e08c40efc08c4012e2e002e3130014968b900010009496861
b0405258381137b90009ffc03859330337133713371701ba76445f18c314
42feed02950efdb33e01d53e17fd7000>/TTE151EA88t00 AddT42Char 
1 11724 72 <0002001ffff6017e01e30013001a004e401c011b1b401c0a141114120d03
16050a0f070018070707010000010346762f3718003f3f10fd10fd012ffd
2e2e2e2e002e2e3130014968b90003001b496861b0405258381137b9001b
ffc038591722263534373633321615140623143332371706033235342322
06c64d5a413c563e4e86986e39341a41afd54c314b0a6d5d85524c45384f
48991d38250119514452>/TTE151EA88t00 AddT42Char 
1 14242 85 <0001001b0000012f01e300140052401e01151540160c0d07140c04010007
0605020f060a0a010501010000010146762f3718003f3c3f3f10fd012ffd
3c2e2e2e2e2e002e2e3130014968b900010015496861b0405258381137b9
0015ffc038593323132f013717153e013332170726232207060f01664b30
050946050b4e340d0d0f090d382b2e0d06015a3e3e0a3e333341034b054a
4e863e00>/TTE151EA88t00 AddT42Char 
1 14396 86 <00010000fff6014c01e30022004f401d0123234024121303120208051f18
050d05070015071010010000010246762f3718003f3f10fd10fd012ffd2f
fd2e2e002e2e3130014968b900020023496861b0405258381137b90023ff
c03859172227371633323635342f0126353436333217072623220615141f
0116171615140706884741253537232d49076652454d3d25303e222b4808
2b1c1e282b0a2a36222d252e1f032c533e502836202b242f1b031022252a
3f292a00>/TTE151EA88t00 AddT42Char 
1 12610 76 <00020018000000c302ca0007001300614025011414401511070504030100
0100010208060505060b041108060e0e050400010001010546762f371800
3f3c3f3c2f10fd012ffd872e0ec40efc08c4012e2e2e2e2e2e0031300149
68b900050014496861b0405258381137b90014ffc0385913330307152313
3f0122263534363332161514064d4b31044b310440141c1e17151c2001d9
fea33e3e015d3ec91d16151e1d16151e>/TTE151EA88t00 AddT42Char 
1 13694 82 <0002001efff401a501e5000c001800474019011919401a09160503100509
0d070013070606010000010346762f3718003f3f10fd10fd012ffd2ffd00
3130014968b900030019496861b0405258381137b90019ffc03859172226
35343633321615140706273236353426232206151416cb4e5f75644e603a
3c6140553d3141543e0c6c5a82a96c5a8252573f7965435278654353>/TTE151EA88t00 AddT42Char 
1 13490 81 <0001001b0000019801e3001d006f402e011e1e401f0c061d1c1211100300
12111213080f0e0e0f0605050115050c170609090104011d121103000001
0046762f3718003f173c3f3f10fd012ffd2ffd3c872e0ec40efc08c4012e
2e2e2e2e2e2e002e3130014968b90000001e496861b0405258381137b900
1effc0385933132f013717153e0133321615140f02152313363534232207
060f01151b30050946050e53343a41031b044e2d02423c2e320d06015a3e
3e0a3e333341483f1314b93e3e01310f0d534a508a3e3e00>/TTE151EA88t00 AddT42Char 

1 0 3 <> /TTE151EA88t00 AddT42Char 
1 2958 20 <000100150000014f01e8001000b9405b01111140120f05040f0d0a060400
030203040a04051010000f0f10030203040804050c0b0b0c030203040804
050d0d0e0c0c0d0403040508050607060607040304050805060807070803
07000f0e02030107000901100000010046762f3718003f3c3f10fd173c10
fd01872e0ec408fc08c4872e0ec408fc08c4872e08c408fc08c4872e0ec4
08fc08c4872e08c408fc08c4012e2e2e2e2e2e002e2e3130014968b90000
0011496861b0405258381137b90011ffc03859333f021307273f02170f01
031f010715093c3e2c7e184b4c450b0d09283b3d083a0204013a343d1e21
260c3e3efee004023a00>/TTE151EA88t00 AddT42Char 
1 2600 17 <00010009fff3007c0066000b0037400f010c0c400d090904030600000103
46762f3718003f2f012ffd003130014968b90003000c496861b040525838
1137b9000cffc038591722263534363332161514063f171f2419171f240d
1e1719251f171924>/TTE151EA88t00 AddT42Char 
1 2804 19 <00020022fff601c101e3000e001a00474019011b1b401c0b18050412050b
0f070015070707010000010446762f3718003f3f10fd10fd012ffd2ffd00
3130014968b90004001b496861b0405258381137b9001bffc03859172227
263534363332171615140706273236353426232206151416e15533377967
5533373b3e604458463f455b480a373b68789b373a67784d503a7462485b
72614a5c>/TTE151EA88t00 AddT42Char 
1 0 0 <0002003f000001b602ee0003000700564020010808400902070405010006
0505030205040700070607010201030000010046762f3718003f3c2f3c10
fd3c10fd3c012f3cfd3c2f3cfd3c003130014968b900000008496861b040
5258381137b90008ffc0385933112111253311233f0177fec7fafa02eefd
123f0271>/TTE151EA88t00 AddT42Char 
/TTE151EA88t00 findfont /CharStrings get begin
/V 57 def
/e 72 def
/r 85 def
/s 86 def
/i 76 def
/o 82 def
/n 81 def
/space 3 def
/one 20 def
/period 17 def
/zero 19 def
/.notdef 0 def
end
/TTE151EA88t00 findfont /Encoding get
dup 1 /V put
dup 2 /e put
dup 3 /r put
dup 4 /s put
dup 5 /i put
dup 6 /o put
dup 7 /n put
dup 8 /space put
dup 9 /one put
dup 10 /period put
dup 11 /zero put
pop
F /F2 0 /0 F /TTE151EA88t00 mF 
/F2S83 F2 [131 0 0 -131 0 0 ] mFS
F2S83 Ji 
1344 1129 M <0102030405060708090A0B>[65 60 44 51 30 66 66 33 60 32  0]xS 
30000 VM?
Pscript_WinNT_Compat begin /$x mysetup def end
Pscript_WinNT_Incr dup /suspend get exec
Pscript_WinNT_Compat dup /initialize get exec
%%BeginDocument: Pscript_Win_PassThrough
1338 998/G1378995 FmND

%%EndDocument

Pscript_WinNT_Compat dup /suspend get exec
Pscript_WinNT_Incr dup /resume get exec
/DeviceRGB dup setcolorspace /colspABC exch def
0 0 0 1 scol 626 VM?
1 2302 17 <00010026fff500bf008e000b0037400f010c0c400d090904030600000103
46762f3718003f2f012ffd003130014968b90003000c496861b040525838
1137b9000cffc0385917222635343633321615140672202c2d1f212c2d0b
2c20202d2d20202c>/TTE16B5380t00 AddT42Char 
1 2640 20 <000100150000018801e600100050401c01111140120f0504100f0a060100
0403040d0b0c0901100000010646762f3718003f3c3f012f3c3cfd3c2e2e
2e2e2e2e002e2e3130014968b900060011496861b0405258381137b90011
ffc0385933353f013507273f02170f01151f01152c3a3974164d554c1804
02393a670204f62169121514173e3ee604026700>/TTE16B5380t00 AddT42Char 
/TTE16B5380t00 findfont /CharStrings get begin
/period 17 def
/one 20 def
end
/TTE16B5380t00 findfont /Encoding get
dup 16 /period put
dup 17 /one put
pop
/F1S83 F1 [131 0 0 -131 0 0 ] mFS
F1S83 Ji 
819 1402 M <011011>[65 34  0]xS 
626 VM?
1 10340 71 <0002001efff201d102d10015002000604026012121402212140919181210
0f0d0c0a091e04041b070716070013000d0c0307010000010446762f3718
003f3f3f3c3f10fd10fd012ffd2e2e2e2e2e2e2e2e2e002e2e3130014968
b900040021496861b0405258381137b90021ffc038591722272635343633
3217352f01330f01111f010727062732373526232206151416ce4e30326f
572e2d02048104020c117d0c2b3030221e242d39300b3f4374708812843e
3e3e3efe333e3e1a45426b25e6124c3e454e>/TTE16B5380t00 AddT42Char 
1 13328 88 <0001002afff201cc01d9001800574021011919401a1517151312100f0d09
0806050b07001600100f060305010000010546762f3718003f3f173c3f10
fd012e2e2e2e2e2e2e2e2e2e002e3130014968b900050019496861b04052
58381137b90019ffc03859172226350327330f0115143332370327330f01
151f01072706b94049020482040246351902048204020c107d0b330a524c
01073e3e3ea6542b010d3e3e3ed53e3e1a454100>/TTE16B5380t00 AddT42Char 
/TTE16B5380t00 findfont /CharStrings get begin
/d 71 def
/u 88 def
end
/TTE16B5380t00 findfont /Encoding get
dup 18 /d put
dup 19 /u put
pop
1344 1402 M <0607080A0E12130C080D0E07>[34 68 48 47 68 69 67 58 48 33 69  0]xS 
30000 VM?
Pscript_WinNT_Compat begin /$x mysetup def end
Pscript_WinNT_Incr dup /suspend get exec
Pscript_WinNT_Compat dup /initialize get exec
%%BeginDocument: Pscript_Win_PassThrough
813 1271/G1379070 FmND

%%EndDocument

Pscript_WinNT_Compat dup /suspend get exec
Pscript_WinNT_Incr dup /resume get exec
/DeviceRGB dup setcolorspace /colspABC exch def
0 0 0 1 scol 22725 VM?
-0.191 -0.258 1.238 0.938
 256 array 0 1 255 {1 index exch /.notdef put} for  /TTE153FD90t00
Type42DictBegin
[<00010000000a000a000a000a6376742000e4077f000000ac0000001a6670
676d324d7366000000c800000162676c7966b49d1f54000008dc00014e20
68656164dda23e6b0000022c000000366868656107f40445000002640000
0024686d7478db25191d00000288000003d86c6f6361413af44800000660
000001ee6d617870030804970000085000000020707265709ad0d7aa0000
08700000006a676469720000000000000000000000000014002600380042
004a0000001701bf001a028e001702ca00060000b800002c4bb800095058
b101018e59b801ff85b800441db9000900035f5e2db800012c2020456944
b001602db800022cb800012a212db800032c2046b003254652582359208a
208a49648a204620686164b004254620686164525823658a592f20b00053
586920b000545821b040591b6920b000545821b0406559593a2db800042c
2046b00425465258238a592046206a6164b0042546206a61645258238a59
2ffd2db800052c4b20b0032650585158b080441bb04044591b21212045b0
c05058b0c0441b2159592db800062c2020456944b001602020457d691844
b001602db800072cb800062a2db800082c4b20b003265358b0801bb04059
8a8a20b0032653582321b0c08a8a1b8a235920b0032653582321b801008a
8a1b8a235920b0032653582321b801408a8a1b8a235920b80003265358b0
032545b8018050582321b8018023211bb003254523212321591b2159442d
b800092c4b535845441b2121592d00000001000000010000c77cfd825f0f
3cf5001903e800000000bb62555000000000befaa58dff3ffefd04d903ab
0000000900020000000000000001000003abfefd002e0514ff3fff3d04d9
0001000000000000000000000000000000f601f400000000000000c80000
00c8000000dc00390138003801f4002701f4006d0384003402fb004000b4
0038013600270136001301f4002a01f4002500cb002600fa001700cb002d
012b001401f4002601f4005f01f4003801f4004601f4001b01f4005301f4
003d01f4004701f4003801f4003800cb002c00cb002401f4007201f4002b
01f400740190001c03e8004a02a40008022200220280003002c300220228
002201f6002202af003002e10022014700220139001b029b002202060022
0372002202d3002202bd003001fb002202bd0030024c002201ee002c0245
000c02c90016029d00080406000802640009024f00080229002d00ea003d
012b001400ea0014012b001001f4fffc0000ff3f01cd002a020c000701b7
002e0209002e01c3002e0138001e01e8002b0243001a0127001a01070011
021c00170127001a0356001a023e001a0205002e0222001c01f6002e0170
001a0180002a014c001c023c001a01f7000b02f6000b01fc001c01f7000c
01b6002b012100100064001c0121001601f4004501f9001300cb002601f4
ffe6016900260264002d01f4005201f400530000ff7a0514003a01ee002c
014d002b038400300229002d00cb002600cb00260169002601690027014d
004701f4fffc03e8fffc0000ff69039d003a0180002a014d00380322002e
01b6002b024f000800dc003901b7002e01f4001d01f9001301ae000603e8
000001f400650000ff7e0341003e0189003a01f4001b015900240341003e
0000ff7a013400380382001e03e400060000ffeb01480027021a003000cb
002d0000ffda022a001e01b0003501f4002803e8000003e8000003e80000
0190001c02a4000802a4000802a4000802a4000802a4000802a400080377
000802800030022800220228002202280022022800220147ffe201470022
0147001d0147002102c3001502d3002202bd003002bd003002bd003002bd
003002bd003003e8000002bd003002c9001602c9001602c9001602c90016
024f0008020e0022022c001f01cd002501cd002a01cd002a01cd002a01cd
002a01cd002a02b8002a01b7002e01c3002001c3002e01c3002e01c3002e
0127ffd20127001a0127000d012700110209002e023e001a0205002e0205
002e0205002e0205002e0205002e01f400350205002e023c001a023c001a
023c001a023c001a01f7000c0222001c01f7000c0206001c0127001a03e8
00000268001e037a001e0172001801100023014500270159001d0390001e
0341003e016500150143003001f4000601450023015a001a0260001e0070
ffb6024b001e0252001e03fa00000127001a0000ff650000ffc60000ffb1
0000ffb10000ff9b0000ff7900000000000000000000008400b201420204
03c804da04fa055805ba06000640068e06a207140736087408a6093e0a5e
0ab40b6c0c7a0ca00e3e0f500fe2104e1072109210b6118a12ea13341412
151a15ae161c167a178c1804183a186c18d8191a19a21a141b521bea1d3e
1de21f421f902036207c20f2217421ce220e222e22502274228c22a022b4
238824962576266c2772280c29922a362ab02b202b882bba2c6c2ce82e1a
2f16301e30a63208328e3330337433e6346434a434e2353c355a35b235e4
36fe374c37cc387e397439a83a1e3a363c863dfa3e103f763fda40304086
413c421242624276428a42c0438044f6450c46c6472647b2481448a44938
4a524aca4cec4df04e524f9a5034505851105224523852d053cc5536554a
55fa5650569856d857945876589a5abc5cde5f005fd06024607860ce613e
61b462966338647e64f6656e65ea668666c46702674467ac685a68f26a3a
6b826ccc6e306f9a71bc733273e2749275447618767a770e788079647a42
7b307c427d7a7eee8072815e826e837e849a85fa862c865e8694871c883a
88e68a208b5a8ca08dfe8f868fca913291cc9266930893f4943e956095fa
965c96aa98cc99d29aec9bd49c089d089e0e9f52a060a0a6a114a144a16a
a25ea34ca36ea442a4fca4fca52ea558a5caa67aa698a6f8a71000000001
000000f60115000700000000000100000000000a00000200ffff00000000
b800002b00ba0001000200022b01ba0003000200022b01be000300540041
00330027001800082bbe0004004b004100330027001800082b00be000100
910077005d0042002800082bbe000200630051003f0027001800082b00ba
0005000400072bb8000020457d691844000000>
[16161 16333 16149 16349 15937 4613 ] AllocGlyphStorage
]def 
108 44 
PrepFor2015
AddFontInfoBegin
AddFontInfo
/OrigFontType /TrueType def 
/OrigFontName <50726F666F726D612D426F6F6B> def
/OrigFontStyle () def
/FSType 0 def
AddFontInfoEnd
[16#2c 16#4c022476 ] AddXUID
Type42DictEnd
1 16004 55 <0001000c00000239028e00130055bb000f0004000200042b00b800004558
b800082f1bb9000800093e59b800004558b800122f1bb9001200053e59b8
000810b900030001fcb8000810b80006dcb8000ad0b8000bd0b8000310b8
000dd0b8000ed030313f0211230f0123372117232f0123111f011521a152
0b536215280b02170b281562520a52fefd1b106501d70a699a9a690afe29
65101b000000>/TTE153FD90t00 AddT42Char 
2 5124 75 <0001001a0000022e02ca002500d7b800262fb800272fb8002610b80002d0
b800022fb900210003fcb80008d0b8002710b80010dcba00090002001011
1239b900170003fc00b800004558b800072f1bb90007000b3e59b8000045
58b8000c2f1bb9000c00073e59b800004558b800132f1bb9001300053e59
b800004558b800242f1bb9002400053e59ba000900130007111239b8000c
10b9001b0002fc410500d9001b00e9001b00025d411b0008001b0018001b
0028001b0038001b0048001b0058001b0068001b0078001b0088001b0098
001b00a8001b00b8001b00c8001b000d5d30313f02112f01353707153e01
3332161d011f011523353f0135342623220e0207151f01152325480b0b53
aa0a1a4d2d424c0b47dd3e0a313515231e180b0b3ede1b0c4b01d44c0c1b
1184c2262a4f4ac94b0c1b1b0c4bb436400d13180ce64b0c1b000000>/TTE153FD90t00 AddT42Char 
2 5452 76 <0002001a0000010c02ba000b0019009ebb00090004000300042b411b0006
000900160009002600090036000900460009005600090066000900760009
008600090096000900a6000900b6000900c60009000d5d410500d5000900
e5000900025dba000e00030009111239b8000e2fb8000910b80013d0b800
132fb8000e10b900150003fcb8000910b80016d0b800162f00b800004558
b800182f1bb9001800053e59bb00060002000000042b3031132226353436
333216151406033f01352f01353707151f0115238f16201f1716201e8349
0a0a53aa0a0a48e8024e1d19171f20171520fdcd0c4bda4c0c1b1184da4b
0c1b0000>/TTE153FD90t00 AddT42Char 
2 8748 86 <0001002affeb015601d90035022cb800362fb800372fb8003610b80016d0
b800162fb80004d0b800042fb8001610b900270003fc4117000600270016
002700260027003600270046002700560027006600270076002700860027
0096002700a60027000b5d410500b6002700c6002700025d410500d50027
00e5002700025db80006d0b8003710b80031dcb9000c0003fc410500da00
0c00ea000c00025d411b0009000c0019000c0029000c0039000c0049000c
0059000c0069000c0079000c0089000c0099000c00a9000c00b9000c00c9
000c000d5db8003110b8001fd0b8001f2fb8000c10b80021d0b800212f00
b800004558b8001b2f1bb9001b00073e59b800004558b800002f1bb90000
00053e59b900090001fc4121000700090017000900270009003700090047
0009005700090067000900770009008700090097000900a7000900b70009
00c7000900d7000900e7000900f7000900105d411b000700090017000900
270009003700090047000900570009006700090077000900870009009700
0900a7000900b7000900c70009000d71410500d6000900e60009000271b8
001b10b900240001fc410500d9002400e900240002714121000800240018
002400280024003800240048002400580024006800240078002400880024
0098002400a8002400b8002400c8002400d8002400e8002400f800240010
5d411b000800240018002400280024003800240048002400580024006800
2400780024008800240098002400a8002400b8002400c80024000d713031
1722262f0133171e0133323635342e02272e0335343e023332161f012327
2e0123220615141e02171e0315140e02b718472309251315281b2d34141f
2915152e261916283a251f40220a2515162c192b2e121e25121b32271717
2a3b150e0e6e4b0a0b2b26182016100708131c2b211d3226160d0f6f4e09
0a2426161e140e0709141e2e231e35271700>/TTE153FD90t00 AddT42Char 

1 0 3 <> /TTE153FD90t00 AddT42Char 
2 2572 70 <0001002effeb018f01d40021015abb00180003000500042b411b00060018
001600180026001800360018004600180056001800660018007600180086
00180096001800a6001800b6001800c60018000d5d410500d5001800e500
1800025d00b800004558b8000a2f1bb9000a00073e59b800004558b80000
2f1bb9000000053e59b8000a10b900150001fc410500d9001500e9001500
027141210008001500180015002800150038001500480015005800150068
001500780015008800150098001500a8001500b8001500c8001500d80015
00e8001500f8001500105d411b0008001500180015002800150038001500
480015005800150068001500780015008800150098001500a8001500b800
1500c80015000d71b8000010b9001b0002fc411b0007001b0017001b0027
001b0037001b0047001b0057001b0067001b0077001b0087001b0097001b
00a7001b00b7001b00c7001b000d5d410500d6001b00e6001b00025d3031
17222e0235343e0233321e021f0123272e0123220615141633323637170e
01e92a44321b1d364b2e101c1e23150b2317132a1a3c4950491a3c1d1228
5415213d5837385c432502050907724d080c5b615e7113141a202300>/TTE153FD90t00 AddT42Char 
2 1608 68 <0002002affeb01bc01d7001f002a012ab8002b2fb8002c2fb8002b10b800
03d0b800032fb8002c10b80017dcb900080003fcb8001cd0b8001c2fba00
1d00170008111239b8000810b80023d0b8000310b900280003fc411b0006
002800160028002600280036002800460028005600280066002800760028
008600280096002800a6002800b6002800c60028000d5d410500d5002800
e5002800025d00b800004558b800122f1bb9001200073e59b800004558b8
00002f1bb9000000053e59b800004558b8001c2f1bb9001c00053e59bb00
070001002400042bb8001210b9000b0002fc410500d9000b00e9000b0002
5d411b0008000b0018000b0028000b0038000b0048000b0058000b006800
0b0078000b0088000b0098000b00a8000b00b8000b00c8000b000d5db800
1c10b900190002fcb80020d0303117222635343736333534262322060727
3e0133321e021d011f011507370e0127323637352207061514169e314339
3a8c362e1c391a152054331c3023140b46940f1b491e1d3f185f2f2c2615
40393f2d2b413135171817232a0f213525db4b0c1b116d333e392a26661b
1a33202e>/TTE153FD90t00 AddT42Char 
2 7444 83 <0002001cff1101f401d4001d002a0176b8002b2fb8002c2fb8002b10b800
02d0b800022fb900190003fcb80007d0ba000800020019111239b8002c10
b80010dcb900210003fc410500da002100ea002100025d411b0009002100
190021002900210039002100490021005900210069002100790021008900
210099002100a9002100b9002100c90021000d5db8001910b80027d000b8
001c2fb800004558b800072f1bb9000700073e59b800004558b8000b2f1b
b9000b00073e59b800004558b800152f1bb9001500053e59b800004558b8
00182f1bb9001800053e59b8000710b900040002fcb8001510b9001e0001
fc41210007001e0017001e0027001e0037001e0047001e0057001e006700
1e0077001e0087001e0097001e00a7001e00b7001e00c7001e00d7001e00
e7001e00f7001e00105d411b0007001e0017001e0027001e0037001e0047
001e0057001e0067001e0077001e0087001e0097001e00a7001e00b7001e
00c7001e000d71410500d6001e00e6001e000271b8000410b80024d03031
173f01112f013537073e0133321e0215140e0223222627151f0115231332
3635342623220607111e0126470b0b519f0b21572e233a2a1724415b380c
22130a53f2e54c573b38243d1f1724d70f4b01c94c0c1b11562a301f384e
303b654a2a02056f4b0d1a0100706455611d26fec9080800>/TTE153FD90t00 AddT42Char 
2 9456 87 <0001001cffeb013c0249001900babb00100003000500042bb8000510b800
09d0b8001010b8000bd000b8000b2fb800004558b800082f1bb900080007
3e59b800004558b8000c2f1bb9000c00073e59b800004558b800002f1bb9
000000053e59b8000c10b900060001fcb80007d0b8000ed0b8000fd0b800
0010b900130002fc411b0007001300170013002700130037001300470013
005700130067001300770013008700130097001300a7001300b7001300c7
0013000d5d410500d6001300e6001300025d303117222e02351123353735
371533152311141633323637170e01c910211a1151514378781c1c0d2314
102033150917291f01471f10730d8a25fed42a20080c1b1a18000000>/TTE153FD90t00 AddT42Char 
2 3512 72 <0002002effeb019801d4001b0023019eb800242fb800252fb8002410b800
03d0b800032fb8002510b8000bdcb8000310b900100003fc411b00060010
001600100026001000360010004600100056001000660010007600100086
00100096001000a6001000b6001000c60010000d5d410500d5001000e500
1000025db8000cd0b8000c2fb8000b10b9001d0004fcb8001010b80023d0
b800232f00b800004558b800062f1bb9000600073e59b800004558b80000
2f1bb9000000053e59bb001c0001000b00042bb8000010b900130002fc41
1b0007001300170013002700130037001300470013005700130067001300
770013008700130097001300a7001300b7001300c70013000d5d410500d6
001300e6001300025db8000610b900200001fc410500d9002000e9002000
027141210008002000180020002800200038002000480020005800200068
002000780020008800200098002000a8002000b8002000c8002000d80020
00e8002000f8002000105d411b0008002000180020002800200038002000
480020005800200068002000780020008800200098002000a8002000b800
2000c80020000d71303117222635343633321e0207211514061514163332
3637170e030337342623220607f0586a665b273f2c1701feda0154511835
1b1113242529034a382f2f3c0b157b75758418334e350a0509055c6e0e14
1711180f07013f0a3c4142450000>/TTE153FD90t00 AddT42Char 
2 8476 85 <0001001a0000016d01d6001900bdbb00150003000200042bb8001510b800
07d0ba00080002001511123900b800004558b800072f1bb9000700073e59
b800004558b8000a2f1bb9000a00073e59b800004558b8000d2f1bb9000d
00073e59b800004558b800182f1bb9001800053e59ba00080018000a1112
39b8000a10b900110002fc410500d9001100e9001100025d411b00080011
001800110028001100380011004800110058001100680011007800110088
00110098001100a8001100b8001100c80011000d5d30313f02352f013537
073633321617072e0123220607151f01152325480b0b53a00a38560e1110
150b1c0e1a38170b52f21b0c4bda4c0c1b115f6502055105071a1fdf4b0c
1b00>/TTE153FD90t00 AddT42Char 
2 3020 71 <0002002effeb01f902ca001b0028016eb800292fb8002a2fb8002910b800
05d0b800052fb8002a10b80013dcb9000e0003fcb80018d0ba0019001300
0e111239b8000e10b8001fd0b8000510b900260003fc411b000600260016
002600260026003600260046002600560026006600260076002600860026
0096002600a6002600b6002600c60026000d5d410500d5002600e5002600
025d00b800004558b800122f1bb90012000b3e59b800004558b8000a2f1b
b9000a00073e59b800004558b800002f1bb9000000053e59b800004558b8
00182f1bb9001800053e59b900150002fcb8001cd0b8000a10b900230001
fc410500d9002300e9002300027141210008002300180023002800230038
002300480023005800230068002300780023008800230098002300a80023
00b8002300c8002300d8002300e8002300f8002300105d411b0008002300
180023002800230038002300480023005800230068002300780023008800
230098002300a8002300b8002300c80023000d71303117222e0235343e02
33321617352f01353707111f011507370e0127323637112e012322061514
16d6253e2d18223f58351521150b52a90a0b45920d2349171c3a20162518
4f513e151f3951333c63472704057b4c0c1b1184fe2c4b0c1b155d2f2e38
1f2601330909726058600000>/TTE153FD90t00 AddT42Char 
2 9724 88 <0001001affeb022b01d0002000dfb800212fb800222fb8002110b80003d0
b800032fb9000a0003fcb8002210b80017dcb900100003fcb8001bd0ba00
1c0017001011123900b800004558b800082f1bb9000800073e59b8000045
58b800152f1bb9001500073e59b800004558b800002f1bb9000000053e59
b800004558b8001b2f1bb9001b00053e59b8000010b9000d0002fc411b00
07000d0017000d0027000d0037000d0047000d0057000d0067000d007700
0d0087000d0097000d00a7000d00b7000d00c7000d000d5d410500d6000d
00e6000d00025db80018d0b800182fba001c000000081112393031052226
3d012f0135370715141633323637352f01353707151f011507370e030102
414b0b51a90a3332243b170a52a90a0a4a970b13232529155049c84c0c1b
1184b3383e281ae74c0c1b1184da4b0c1b11581823170a00>/TTE153FD90t00 AddT42Char 
2 6228 80 <0001001a0000034301d4003700c7bb00330003000200042bbb0024000300
2b00042bbb00150003001c00042bb8003310b80007d0ba00080002003311
1239ba000e002b0024111239b8001510b80039dc00b800004558b800072f
1bb9000700073e59b800004558b8000b2f1bb9000b00073e59b800004558
b800112f1bb9001100073e59b800004558b800182f1bb9001800053e59b8
00004558b800272f1bb9002700053e59b800004558b800362f1bb9003600
053e59b8000710b900040002fcba000e0018000b111239b80020d0b8002f
d030313f02352f013537073e01333216173e013332161d011f011523353f
0135342623220607151f011523353f0135342623220607151f0115232548
0b0b53a00a204b302e44101c4d303f4c0a48de3e0b323220381d0b3fd53e
0b333123371b0b3ede1b0c4bda4c0c1b11582e2e2d2d2b2f5148c94b0c1b
1b0c4bb436402220e84b0c1b1b0c4bb43640251de84b0c1b0000>/TTE153FD90t00 AddT42Char 
2 10664 92 <0001000cff0601eb01bf0016003300b800162fb800004558b800082f1bb9
000800073e59b800004558b800112f1bb9001100073e59ba000d00160008
111239303117353f02032f01353315071f013f01273533150f0103325f2a
318a2a2cc94318645c1542ae3224daee1d0f467401544d0b1b1b0b4dfefe
4d0b1b1b0b4dfdba>/TTE153FD90t00 AddT42Char 
2 10184 90 <0001000bffee02eb01bf0022007600b800004558b800042f1bb900040007
3e59b800004558b8000d2f1bb9000d00073e59b800004558b800162f1bb9
001600073e59b800004558b8001c2f1bb9001c00053e59b800004558b800
212f1bb9002100053e59ba0009001c0004111239ba0012001c0004111239
ba001f001c00041112393031372f0235331507171b012f0135331507171b
0137273533150f03232f010f0123b757272ec43f1863592434ce4218625c
1444ad31254e2a2f30443a2a2f72da4d0b1b1b0b4dfeff0105490b1b1b0b
4dfeff01014d0b1b1b0b4dda8484acac8400>/TTE153FD90t00 AddT42Char 
2 6584 81 <0001001a0000022b01d40024008bb800252fb800262fb8002510b80002d0
b800022fb900200003fcb80007d0ba000800020020111239b8002610b800
0fdcb900160003fc00b800004558b800072f1bb9000700073e59b8000045
58b8000b2f1bb9000b00073e59b800004558b800122f1bb9001200053e59
b800004558b800232f1bb9002300053e59b8000710b900040002fcb8001a
d030313f02352f013537073e013332161d011f011523353f013534262322
0e0207151f01152325480b0b53a00a204c313f4c0a49df3f0a3332101a1b
1e130b3ede1b0c4bda4c0c1b1158302c5148c94b0c1b1b0c4bb43640050f
1915e84b0c1b0000>/TTE153FD90t00 AddT42Char 
1 12404 45 <0001001bff14011e028e00110022bb000c0004000300042b00b800112fb8
00004558b800072f1bb9000700093e593031173e0135112f013521150f01
11140e02072b2b210a520103520b10213120d32b714501f165101a1a1065
fe35375242391c000000>/TTE153FD90t00 AddT42Char 
1 15300 54 <0001002cffed01c202a5003b0218b8003c2fb8003d2fb8003c10b80018d0
b800182fb80004d0b800042fb8001810b9002d0003fc41170006002d0016
002d0026002d0036002d0046002d0056002d0066002d0076002d0086002d
0096002d00a6002d000b5d410500b6002d00c6002d00025d410500d5002d
00e5002d00025db80006d0b800062fb8003d10b80037dcb9000e0003fc41
0500da000e00ea000e00025d411b0009000e0019000e0029000e0039000e
0049000e0059000e0069000e0079000e0089000e0099000e00a9000e00b9
000e00c9000e000d5d00b800004558b8001d2f1bb9001d00093e59b80000
4558b800002f1bb9000000053e59b900090001fc41210007000900170009
002700090037000900470009005700090067000900770009008700090097
000900a7000900b7000900c7000900d7000900e7000900f7000900105d41
1b0007000900170009002700090037000900470009005700090067000900
770009008700090097000900a7000900b7000900c70009000d71410500d6
000900e60009000271b8001d10b900280001fc410500d9002800e9002800
027141210008002800180028002800280038002800480028005800280068
002800780028008800280098002800a8002800b8002800c8002800d80028
00e8002800f8002800105d411b0008002800180028002800280038002800
480028005800280068002800780028008800280098002800a8002800b800
2800c80028000d7130311722262f0133171e0133323e0235342e02272e03
35343e023332161f0123272e0323220e0215141e02171e0315140e02d824
45350a2c141b311f233c2d1a1c2e3a1e2440321d1b37533726542b0b2d14
0d1e1f1c0a273a27131828331b26483822213e56130b1493680c10111f2e
1e232f22180c0e1e2a392a24443520110f936b080a080314212c171f2c1e
160b0f202e402e2b47331b000000>/TTE153FD90t00 AddT42Char 
1 14972 53 <0002002200000244028e001a002700d0b800282fb800292fb8002810b800
02d0b800022fb8002910b8000adcb8000210b900160004fcb8000a10b900
1e0004fc410500da001e00ea001e00025d411b0009001e0019001e002900
1e0039001e0049001e0059001e0069001e0079001e0089001e0099001e00
a9001e00b9001e00c9001e000d5db8001610b80022d000b800004558b800
062f1bb9000600093e59b800004558b800112f1bb9001100053e59b80000
4558b800192f1bb9001900053e59bb001b0001001400042bb8000610b900
040001fcb80021d0b80022d030313f02112f0135213216151406071f0215
232f0123151f0115231332363534262b01111e023222530a0a5301145955
484568434292465c470a45f6de44564b4c3a090c0b0d1b1065016f65101a
57443f5f14b165101b90a2a265101b01554d453f42fef30202020000>/TTE153FD90t00 AddT42Char 
1 13352 50 <00020030ffeb028d02a4001300270208b800282fb800292fb8002810b800
05d0b800052fb8002910b8000fdcb900190004fc410500da001900ea0019
00025d411b00090019001900190029001900390019004900190059001900
69001900790019008900190099001900a9001900b9001900c90019000d5d
b8000510b900230004fc4117000600230016002300260023003600230046
0023005600230066002300760023008600230096002300a60023000b5d41
0500b6002300c6002300025d410500d5002300e5002300025d00b8000045
58b8000a2f1bb9000a00093e59b800004558b800002f1bb9000000053e59
b900140001fc412100070014001700140027001400370014004700140057
00140067001400770014008700140097001400a7001400b7001400c70014
00d7001400e7001400f7001400105d411b00070014001700140027001400
37001400470014005700140067001400770014008700140097001400a700
1400b7001400c70014000d71410500d6001400e60014000271b8000a10b9
001e0001fc410500d9001e00e9001e00027141210008001e0018001e0028
001e0038001e0048001e0058001e0068001e0078001e0088001e0098001e
00a8001e00b8001e00c8001e00d8001e00e8001e00f8001e00105d411b00
08001e0018001e0028001e0038001e0048001e0058001e0068001e007800
1e0088001e0098001e00a8001e00b8001e00c8001e000d71303105222e02
35343e0233321e0215140e0227323e0235342e0223220e0215141e020153
4c6e47222950764c456c4a27274e76402f503a201a35503634553c211a36
5615325b804e4e815c3330597f4f4e835d34212b5072474876552f294f74
4c4675542f00>/TTE153FD90t00 AddT42Char 
1 11508 42 <00010030ffec02a3029e002d019fbb00180004000500042b411b00060018
001600180026001800360018004600180056001800660018007600180086
00180096001800a6001800b6001800c60018000d5d410500d5001800e500
1800025d00b800004558b8000a2f1bb9000a00093e59b800004558b80000
2f1bb9000000053e59b8000a10b900130001fc410500d9001300e9001300
027141210008001300180013002800130038001300480013005800130068
001300780013008800130098001300a8001300b8001300c8001300d80013
00e8001300f8001300105d411b0008001300180013002800130038001300
480013005800130068001300780013008800130098001300a8001300b800
1300c80013000d71b8000010b9001d0001fc41210007001d0017001d0027
001d0037001d0047001d0057001d0067001d0077001d0087001d0097001d
00a7001d00b7001d00c7001d00d7001d00e7001d00f7001d00105d411b00
07001d0017001d0027001d0037001d0047001d0057001d0067001d007700
1d0087001d0097001d00a7001d00b7001d00c7001d000d71410500d6001d
00e6001d000271303105222e0235343e023332161f0123272e0123220e02
15141e0233323637352f013533150f01150e03017c547c53292755845d2b
5c360b2e142050233b5e43242445653f1d3c200a54fb460b182d333a1433
5b7f4b487f5d360a0c9473090b284b6d45487956300a0f6065101b1b1065
8704080603000000>/TTE153FD90t00 AddT42Char 

1 0 0 <> /TTE153FD90t00 AddT42Char 
/TTE153FD90t00 findfont /CharStrings get begin
/T 55 def
/h 75 def
/i 76 def
/s 86 def
/space 3 def
/c 70 def
/a 68 def
/p 83 def
/t 87 def
/e 72 def
/r 85 def
/d 71 def
/u 88 def
/m 80 def
/y 92 def
/w 90 def
/n 81 def
/J 45 def
/S 54 def
/R 53 def
/O 50 def
/G 42 def
/.notdef 0 def
end
/TTE153FD90t00 findfont /Encoding get
dup 1 /T put
dup 2 /h put
dup 3 /i put
dup 4 /s put
dup 5 /space put
dup 6 /c put
dup 7 /a put
dup 8 /p put
dup 9 /t put
dup 10 /e put
dup 11 /r put
dup 12 /d put
dup 13 /u put
dup 14 /m put
dup 15 /y put
dup 16 /w put
dup 17 /n put
dup 18 /J put
dup 19 /S put
dup 20 /R put
dup 21 /O put
dup 22 /G put
pop
F /F3 0 /0 F /TTE153FD90t00 mF 
/F3S41 F3 [65 0 0 -65 0 0 ] mFS
F3S41 Ji 
1344 1577 M <010203040506020708090A0B050C0304060D04040A04050304040D0A040509020709050E070F05070B03040A0510020A11051213140405070B0A050D040A0C050311050711051513160305>
[38 38 19 25 13 29 38 30 36 22 29 25 13 34 19 25 29 38 25 25 30 25 13 19 25 25 38 30 25 13 22 37
31 21 14 56 30 33 13 30 24 19 26 29 13 50 38 30 37 13 21 32 39 25 13 30 24 30 13 37 26 29 34 13
20 37 13 31 37 13 46 33 45 19  0]xS 
30000 VM?
Pscript_WinNT_Compat begin /$x mysetup def end
Pscript_WinNT_Incr dup /suspend get exec
Pscript_WinNT_Compat dup /initialize get exec
%%BeginDocument: Pscript_Win_PassThrough
1338 1512/G1379071 FmND

%%EndDocument

Pscript_WinNT_Compat dup /suspend get exec
Pscript_WinNT_Incr dup /resume get exec
/DeviceRGB dup setcolorspace /colspABC exch def
0 0 0 1 scol 2403 VM?
2 10048 89 <0001000bffee01ea01bf0014004000b800004558b800042f1bb900040007
3e59b800004558b8000d2f1bb9000d00073e59b800004558b800132f1bb9
001300053e59ba0009001300041112393031372f0235331507171b013727
3533150f0323b8582a2bc74319655e1444af3125532b2d72da4d0b1b1b0b
4dfeff01014d0b1b1b0b4dda84000000>/TTE153FD90t00 AddT42Char 
2 6832 82 <0002002effea01d701d50011001d0208b8001e2fb8001f2fb8001e10b800
05d0b800052fb8001f10b8000ddcb900150004fc410500da001500ea0015
00025d411b00090015001900150029001500390015004900150059001500
69001500790015008900150099001500a9001500b9001500c90015000d5d
b8000510b9001b0004fc41170006001b0016001b0026001b0036001b0046
001b0056001b0066001b0076001b0086001b0096001b00a6001b000b5d41
0500b6001b00c6001b00025d410500d5001b00e5001b00025d00b8000045
58b8000a2f1bb9000a00073e59b800004558b800002f1bb9000000053e59
b900120001fc412100070012001700120027001200370012004700120057
00120067001200770012008700120097001200a7001200b7001200c70012
00d7001200e7001200f7001200105d411b00070012001700120027001200
37001200470012005700120067001200770012008700120097001200a700
1200b7001200c70012000d71410500d6001200e60012000271b8000a10b9
00180001fc410500d9001800e90018000271412100080018001800180028
001800380018004800180058001800680018007800180088001800980018
00a8001800b8001800c8001800d8001800e8001800f8001800105d411b00
080018001800180028001800380018004800180058001800680018007800
18008800180098001800a8001800b8001800c80018000d71303117222e02
35343e0233321615140e02273236353426232206151416fe2f4d361e1d37
52355f6f1f39502f424b4840424b4716213d5938385d42257d77385b4123
246d67636d6968656e000000>/TTE153FD90t00 AddT42Char 
1 3396 17 <0001002dfff5009d0066000b00b6bb00090004000300042b411b00060009
001600090026000900360009004600090056000900660009007600090086
00090096000900a6000900b6000900c60009000d5d410500d5000900e500
0900025db8000910b8000ddc00b800004558b800002f1bb9000000053e59
b80006dc411b000700060017000600270006003700060047000600570006
0067000600770006008700060097000600a7000600b7000600c70006000d
5d410500d6000600e6000600025d30311722263534363332161514066516
221e1a16221e0b1e1a162320191424000000>/TTE153FD90t00 AddT42Char 
/TTE153FD90t00 findfont /CharStrings get begin
/v 89 def
/o 82 def
/period 17 def
end
/TTE153FD90t00 findfont /Encoding get
dup 23 /v put
dup 24 /o put
dup 25 /period put
pop
1344 1658 M <0A1117030B18110E0A110919>[29 38 33 19 24 34 38 56 29 38 22  0]xS 
F1S83 Ji 
819 1873 M <011001>[65 34  0]xS 
1251 VM?
1 5432 38 <00010023fff2022102a80015004a401a011616401708130914080e040311
06000b060606020000010346762f3718003f3f10fd10fd012ffd2e2e002e
2e3130014968b900030016496861b0405258381137b90016ffc038590522
2635343633321707262322061514163332371706015e90abb49561542d40
50556d655b4a49275f0eb5a29bc42870237d69697e237027>/TTE16B5380t00 AddT42Char 
1 6250 43 <0001003100000241029a0017006c402c01181840190c1715141312100f0e
0c0b090807060403020014130608070c0b0403030217100f030000010046
762f3718003f173c3f173c2f3cfd3c012e2e2e2e2e2e2e2e2e2e2e2e2e2e
2e2e2e2e003130014968b900000018496861b0405258381137b90018ffc0
3859333f0103330f011521352f01330f0113233f013521151f0131040206
8404020115020483040206830402feeb02043e3e021e3e3e95953e3e3e3e
fde23e3e9b9b3e3e>/TTE16B5380t00 AddT42Char 
1 11816 79 <0001002e000000b002d10007004340150108084009040706040302000403
03070000010046762f3718003f3c3f3c012e2e2e2e2e2e003130014968b9
00000008496861b0405258381137b90008ffc03859333f0103330f01132e
040206820402063e3e02553e3efdab00>/TTE16B5380t00 AddT42Char 
1 10892 74 <00030016ff1f01be020900250031003b006b402d013c3c403d141c111514
0a053a05032905172f050c3504231e04072c070f1a072632070013003800
0f01010346762f3718003f3f2f2f10fd2ffd10fd012ffd2ffd2ffd2ffd2f
fd2e2e2e2e002e2e3130014968b90003003c496861b0405258381137b900
3cffc038591722263534372635343637263534363332173f011707161514
062322270615141f01161514060332363534262322061514161332363534
2f01061514d8566c4637251e466653332b1c37324015634f111c184027af
795f1e28261f1f272626283341472de1453e511f173a1f330a29534b5d12
11275b261f29485a050b15180a061b7a4a5d01de241d2126241e2026fe85
1f1b270b0c17253c>/TTE16B5380t00 AddT42Char 
/TTE16B5380t00 findfont /CharStrings get begin
/C 38 def
/H 43 def
/l 79 def
/g 74 def
end
/TTE16B5380t00 findfont /Encoding get
dup 20 /C put
dup 21 /H put
dup 22 /l put
dup 23 /g put
pop
1344 1873 M <0203040501111105140E070809070805150B0712160D0717>[34 67 70 30 66 58 59 30 78 69 68 48 65 68 49 30 86 62 68 68 33 33 68  0]xS 
30000 VM?
Pscript_WinNT_Compat begin /$x mysetup def end
Pscript_WinNT_Incr dup /suspend get exec
Pscript_WinNT_Compat dup /initialize get exec
%%BeginDocument: Pscript_Win_PassThrough
813 1742/G1379072 FmND

%%EndDocument

Pscript_WinNT_Compat dup /suspend get exec
Pscript_WinNT_Incr dup /resume get exec
/DeviceRGB dup setcolorspace /colspABC exch def
0 0 0 1 scol 2403 VM?
2 1276 62 <0001003dff4700db02c000090015bb00040003000000042b00b800002fb8
00082f30311333150f01111f0115233d9e550b0b559e02c01a1066fda865
101c0000>/TTE153FD90t00 AddT42Char 
1 4328 20 <0001005f000001a2028e000c002fbb00080004000200042b00b800004558
b800072f1bb9000700093e59b800004558b8000b2f1bb9000b00053e5930
313f02112f013537111f0115216a6c0b0b77cb0b6dfec81b106501596710
1915fe0265101b000000>/TTE153FD90t00 AddT42Char 
2 1408 64 <00010014ff4700b102c00009001dbb00080003000200042bb8000810b800
0bdc00b800062fb800082f3031173f01112f013533112314550b0b559d9d
9d1065025866101afc870000>/TTE153FD90t00 AddT42Char 
/TTE153FD90t00 findfont /CharStrings get begin
/bracketleft 62 def
/one 20 def
/bracketright 64 def
end
/TTE153FD90t00 findfont /Encoding get
dup 26 /bracketleft put
dup 27 /one put
dup 28 /bracketright put
pop
F3S41 Ji 
1344 2048 M <01020A051A1B1C05>[38 38 29 14 15 33 15  0]xS 
19338 VM?
-0.129 -0.246 1.25 0.953
 256 array 0 1 255 {1 index exch /.notdef put} for  /TTE1AD8890t00
Type42DictBegin
[<00010000000a000a000a000a6376742000dd0789000000ac0000001a6670
676d324d7366000000c800000162676c7966cae91509000008dc000138d8
68656164dde93e720000022c0000003668686561092007a3000002640000
0024686d7478cb28347800000288000003d86c6f63612d80e3e200000660
000001ee6d6178700308049f00000850000000207072657092ded3c00000
08700000006a6764697200000000000000000000000000140025003d0045
003d0000001d01c9000f028e001c02c800070000b800002c4bb800095058
b101018e59b801ff85b800441db9000900035f5e2db800012c2020456944
b001602db800022cb800012a212db800032c2046b003254652582359208a
208a49648a204620686164b004254620686164525823658a592f20b00053
586920b000545821b040591b6920b000545821b0406559593a2db800042c
2046b00425465258238a592046206a6164b0042546206a61645258238a59
2ffd2db800052c4b20b0032650585158b080441bb04044591b21212045b0
c05058b0c0441b2159592db800062c2020456944b001602020457d691844
b001602db800072cb800062a2db800082c4b20b003265358b0801bb04059
8a8a20b0032653582321b0c08a8a1b8a235920b0032653582321b801008a
8a1b8a235920b0032653582321b801408a8a1b8a235920b80003265358b0
032545b8018050582321b8018023211bb003254523212321591b2159442d
b800092c4b535845441b2121592d000000010000000100001f16c5665f0f
3cf5001903e800000000bb62555000000000befaa57bff7dff0604e203bb
0000000900020000000000000001000003bbff06002e0514ff7dfeab04e2
03e800d500000000000000000000000000f601f400000000000000c80000
00c8000000dc003b013800a101f4003701f4006e038400a402b2005e00b4
009e013600480136ffef01f4004501f4004d00cb001000fa004700cb002f
0145000d01f4005a01f4006c01f4003301f4004101f4004e01f4004d01f4
006701f400a401f4005001f4006300cb002f00cb001301f4009201f40044
01f400920190008603e8007102a4000f022200230280005a02b200230228
002301f6002302af005a02dd00230148002301390003029b002302060023
0372002302d3002302bd005b0208002302bd005b024c002301f90032024a
007102c9008f029d0081040400810264000a024c00810229002d00eaffff
00fe007900eaffe90102005601f4ffcf0000ff9b01e8005301d100490169
004901e600530173004900fcff7d01d1001401e2004900fc005b00f1ffcd
01bf004900fc005a02f6005501f50055019d004901e3002301cd00530184
00550162002c0118006801f7005701d9005b02ef005b01f0001d01deffe7
01b8002c0121004a006400180121fff201f4006901f5003300cb001001f4
fff10187001002a7003201f400aa01f400660000ffd2051400ad01f90032
014d0056039f005b0229002d00cb007e00cb0077018700820187007d014d
007101f4002903e800290000ffdd039d009a0162002c014d003e027c0049
01b8002c024c008100dc00430169004901f4001b01f5003301b8005e03e8
000001f4005b0000fff60341006a0190008a01f40046015900770341006a
0000fffb012c00a002e7ff7d03d2002d0000005401480066021a008f00cb
00550000ffbd01efff7d0158008201f4002d03e8000003e8000003e80000
0190004102a4000f02a4000f02a4000f02a4000f02a4000f02a4000f037a
00120280005a022800230228002302280023022800230148002301480023
014800230148002302b2002302d3002302bd005b02bd005b02bd005b02bd
005b02bd005b03e8000002bd005b02c9008f02c9008f02c9008f02c9008f
024c008102130023020fff7d01e8005301e8005301e8005301e8005301e8
005301e80053028200510169004901730049017300490173004901730049
00fc001900fc005b00fc005000fc005b01e6005301f50055019d0049019d
0049019d0049019d0049019d004901f4003301a1004001f7005701f70057
01f7005701f7005701deffe701e7002301deffe70206002300fb004a03e8
000001f5ff7d02e9ff7d0172007901100062014500460159005702f9ff7d
0341006a016500510143003f01f4005401450069015a00650201ff7d0083
ffa801f0ff7d01f0ff7d03ec000000fc005b0000ffde0000003100000014
000000090000ff880000ffe700000000000000000000008000ae0120024e
0416059005aa05f80648069406c407120724079607b808f6092409de0b08
0b520c0e0d200d460f04101010c6115a117e119e11c2129413c0140a14e4
15fa168016de1728184018a218d41902196a19a01a161a721bbe1c441db0
1e4c1f861fce20ac20f2216821d6222422662282229c22b822d822ec2300
2388241c24ac2544265626bc275627be282e28a228fe292a29d42a362b2c
2bb62c3c2c862d822dcc2e322eb62f8c2ff0307c30d03124313e318e31be
32de332e3396342c35243556359a35ba380e3954396a3aba3b1a3b723bca
3c703d123d5e3d723d863dbe3e623f823f98416241c4424042a2433243c2
44e245644786488c48ee49e84a644a8a4b384c3c4c564d3c4e4c4fb64fca
508050c6510e516a521652de53025524574659685a3a5a8e5ae25b3a5bac
5c225d045d865ef85f605fc8603460c060fa6134617261d0627262f6644a
65a466fa686c69e46c066d246e0c6ef46fde70ea714271c8736273f47486
752075d676b877f2799e7a627b7e7c9a7dcc7f3e7f707fa27fde8060810e
8198829c839c84aa85ce8726875e885c88c4892c899e8a588adc8b6a8c46
8c968cd88efa8fdc90c891b691de92dc93e295209624966296da970a972e
98409906992899be9a649a649a909ab69b2a9be09bfe9c469c6c00000001
000000f60115000700000000000100000000000a00000200ffff00000000
b800002b00ba0001000200022b01ba0003000200022b01be000300500047
00380028001800082bbe0004005b004700380028001800082b00be000100
95007a005f0044002900082bbe0002005b004700380028001800082b00ba
0005000400072bb8000020457d691844000000>
[16285 16277 16273 16317 14941 ] AllocGlyphStorage
]def 
108 44 
PrepFor2015
AddFontInfoBegin
AddFontInfo
/OrigFontType /TrueType def 
/OrigFontName <50726F666F726D612D426F6F6B4974616C6963> def
/OrigFontStyle () def
/FSType 0 def
AddFontInfoEnd
[16#2c 16#45af574c ] AddXUID
Type42DictEnd
1 12712 45 <00010003ff14019b028e0011001800b800112fb800004558b800072f1bb9
000700093e593031173e01371337273521150f01030e030703323b0e5f09
4f0104561e5d0924303a20d428744601f165101a1a1065fe20314e3f3518
0000>/TTE1AD8890t00 AddT42Char 
1 15512 54 <00010032ffe7023c02aa003b01cfb8003c2fb8003d2fb80037dcb9001200
03fc410500da001200ea001200025d411b00090012001900120029001200
39001200490012005900120069001200790012008900120099001200a900
1200b9001200c90012000d5db8003c10b8001cd0b8001c2fb8003710b800
26d0b800262fb8001c10b9002d0003fc41130006002d0016002d0026002d
0036002d0046002d0056002d0066002d0076002d0086002d00095d410900
96002d00a6002d00b6002d00c6002d00045d410500d5002d00e5002d0002
5d00b800004558b800212f1bb9002100093e59b800004558b800002f1bb9
000000053e59b9000d0002fc411b0007000d0017000d0027000d0037000d
0047000d0057000d0067000d0077000d0087000d0097000d00a7000d00b7
000d00c7000d000d5d410500d6000d00e6000d00025db8002110b9002a00
01fc410500e9002a00f9002a00027141210008002a0018002a0028002a00
38002a0048002a0058002a0068002a0078002a0088002a0098002a00a800
2a00b8002a00c8002a00d8002a00e8002a00f8002a00105d411d0008002a
0018002a0028002a0038002a0048002a0058002a0068002a0078002a0088
002a0098002a00a8002a00b8002a00c8002a00d8002a000e71303117222e
02273733151e0333323e0235342e02272e0335343e02333216170723352e
0123220615141e02171e0315140e02f111323633131c2a1321202114223c
2e1b17242e18253d2c17243f5734336336202a224b263f521525331e1d38
2c1c27466019060c0f09895e090c09041727341e1c271e170b11232a3523
2a48361f15178e6b0e10423c1c281e1a0e0d1f2937242c4f3c230000>/TTE1AD8890t00 AddT42Char 
1 15200 53 <0002002300000248028e001c002900b6bb000c0003002200042bb8000c10
b80012d0b800122f410500da002200ea002200025d411b00090022001900
220029002200390022004900220059002200690022007900220089002200
99002200a9002200b9002200c90022000d5db8000c10b8002bdc00b80000
4558b800062f1bb9000600093e59b800004558b800132f1bb9001300053e
59b800004558b8001b2f1bb9001b00053e59bb001d0001001600042bb800
0610b900040001fcb80025d0b80026d030313f021337273521321e021514
06071f0215232f01230f0117152301323e023534262b01031e0123551f49
0a50010b313f250e6155452e42922b3b4f200942f6011c2944321c414b2f
3612151b1065016f65101a1424311d4a6c11b165101b90a2a265101b0155
182d3f273831fef204020000>/TTE1AD8890t00 AddT42Char 

1 0 3 <> /TTE1AD8890t00 AddT42Char 
1 4680 21 <000100330000020a028e00190120bb00100003000300042b410500da0003
00ea000300025d411b000900030019000300290003003900030049000300
5900030069000300790003008900030099000300a9000300b9000300c900
03000d5db8001010b8001bdc00b800004558b8000d2f1bb9000d00093e59
b800004558b800182f1bb9001800053e59b8000d10b900060001fc410500
e9000600f900060002714121000800060018000600280006003800060048
0006005800060068000600780006008800060098000600a8000600b80006
00c8000600d8000600e8000600f8000600105d411d000800060018000600
280006003800060048000600580006006800060078000600880006009800
0600a8000600b8000600c8000600d80006000e71b8001810b900130002fc
3031373e0135342623220607273e0133321615140607333f0133072133c3
c43b2c2d4c2d122e6d3c4b4db8c8c1521f2025fe7c277ded612f3c232a12
36364c4261de841045920000>/TTE1AD8890t00 AddT42Char 
1 4588 20 <0001006c000001a9028e000c002500b800004558b800072f1bb900070009
3e59b800004558b8000b2f1bb9000b00053e5930313f0213372735370307
1715216c6f1e460972d3660969fec91b1065015967101915fe0265101b00
0000>/TTE1AD8890t00 AddT42Char 
1 10696 38 <0001005affe502b7029f002b01a7bb001a0003000600042b411b0006001a
0016001a0026001a0036001a0046001a0056001a0066001a0076001a0086
001a0096001a00a6001a00b6001a00c6001a000d5d410500d5001a00e500
1a00025d00b800004558b8000c2f1bb9000c00093e59b800004558b80000
2f1bb9000000053e59b8000c10b900150001fc410500e9001500f9001500
027141210008001500180015002800150038001500480015005800150068
001500780015008800150098001500a8001500b8001500c8001500d80015
00e8001500f8001500105d411d0008001500180015002800150038001500
480015005800150068001500780015008800150098001500a8001500b800
1500c8001500d80015000e71b8000010b9001f0001fc41210007001f0017
001f0027001f0037001f0047001f0057001f0067001f0077001f0087001f
0097001f00a7001f00b7001f00c7001f00d7001f00e7001f00f7001f0010
5d411d0007001f0017001f0027001f0037001f0047001f0057001f006700
1f0077001f0087001f0097001f00a7001f00b7001f00c7001f00d7001f00
0e71410500e6001f00f6001f0002713031050626272e01352636373e0133
3216170723352e012322070e011514171e0133323e023f0133070e030173
426a252324014139368a5c325e371d24234e2579562d383c1b4c2f0e262d
301721230b253c37361a0126252463435da139373711118d651014623395
59723e1b1e03080f0c678b0c0f0a0400>/TTE1AD8890t00 AddT42Char 
2 5328 82 <00020049ffee01ab01d500110023017eb800242fb800252fb8002410b800
03d0b800032fb8002510b8000cdcb900160003fc410500da001600ea0016
00025d411b00090016001900160029001600390016004900160059001600
69001600790016008900160099001600a9001600b9001600c90016000d5d
b8000310b900210003fc4117000600210016002100260021003600210046
0021005600210066002100760021008600210096002100a60021000b5d41
0500b6002100c6002100025d410500d5002100e5002100025d00b8000045
58b800092f1bb9000900073e59b800004558b800002f1bb9000000053e59
b900120002fc411b00070012001700120027001200370012004700120057
00120067001200770012008700120097001200a7001200b7001200c70012
000d5d410500d6001200e6001200025db8000910b900190002fc410500d9
001900e9001900025d411b00080019001800190028001900380019004800
19005800190068001900780019008800190098001900a8001900b8001900
c80019000d5d3031172226353436373e01333216151406070e0127323736
353426232206070e03151416a52b31434618441d2e322928285d0b3d2e34
1b231d33180d150e071e1242475e9837131e464a447e2f30363647507235
3c2123133035371a35380000>/TTE1AD8890t00 AddT42Char 
2 5132 81 <00010055fff701fb01d70024005100b800004558b800042f1bb900040007
3e59b800004558b8000b2f1bb9000b00073e59b800004558b800152f1bb9
001500053e59b800004558b800242f1bb9002400053e59ba000600150004
11123930313f012735370f013e01373633321615140f021715073f013e01
353426232206070e010f019115519e0d1f1a3b2242301b260c3406509e0a
3a05041013122e2134470b45eaad0b1a1b588a304e21412e25222dc6350b
1b1b50e411200a141b252b43a4551200>/TTE1AD8890t00 AddT42Char 
2 7016 87 <00010068fff20152023e001d003600b8000c2fb800004558b800002f1bb9
000000053e59bb000d0001001000042bb8001010b80007d0b8000d10b800
09d0b800092f3031172226353436371323353f0207331523030e01151416
33323637170e01ab1d2503023f454e1347195b633c02020f140d220e0f1c
3a0e23220b130b013b1d116c097f24fed108130711120c0c18191c00>/TTE1AD8890t00 AddT42Char 
2 2796 72 <00020049ffef019801d3001c002801a9b800292fb8002a2fb8002910b800
03d0b800032fb900120003fc411b00060012001600120026001200360012
00460012005600120066001200760012008600120096001200a6001200b6
001200c60012000d5d410500d5001200e5001200025db80005d0b800052f
b8002a10b8000adcb8001210b8000fd0b8000f2fb8000a10b900210003fc
410500da002100ea002100025d411b000900210019002100290021003900
2100490021005900210069002100790021008900210099002100a9002100
b9002100c90021000d5db80018d0b800182fba00280003000a11123900b8
00004558b800072f1bb9000700073e59b800004558b800002f1bb9000000
053e59b900140002fc411b00070014001700140027001400370014004700
14005700140067001400770014008700140097001400a7001400b7001400
c70014000d5d410500d6001400e6001400025db8000710b900240002fc41
0500d9002400e9002400025d411b00080024001800240028002400380024
00480024005800240068002400780024008800240098002400a8002400b8
002400c80024000d5dba0028000000071112393031172226353437363332
161514070e01270e01151433323637170e03033237363534262322070607
ac2e354c4d5d26336133551a03043d14432611132c2d2d1a4931321c1728
232117114c4d7b68682e294727140c020f21187c181a1e0e1d170f012219
1a29142026234700>/TTE1AD8890t00 AddT42Char 
1 12416 43 <0001002300000334028e0023005100b800004558b800062f1bb900060009
3e59b800004558b800102f1bb9001000093e59b800004558b800182f1bb9
001800053e59b800004558b800222f1bb9002200053e59bb000c0001001d
00042b30313f021337273521150f02213f01273521150f01030717152135
3f02210f0117152123551f490a500100521e20014d1f0a4d0101561f490a
50ff00521e23feb322094cff001b1065016f65101a1a10659d9d65101a1a
1065fe9165101b1b1065acac65101b00>/TTE1AD8890t00 AddT42Char 
2 1636 68 <00020053fff201eb01c9001600270090bb00260003000a00042b411b0006
002600160026002600260036002600460026005600260066002600760026
008600260096002600a6002600b6002600c60026000d5d410500d5002600
e5002600025d00b800004558b800102f1bb9001000073e59b800004558b8
00072f1bb9000700053e59b800004558b800162f1bb9001600053e59ba00
0100160010111239303125370e01070e01232226353436373e013b010f01
17150727323e02373e013f0127260607061514015820142d17264d211a1f
41432850385d3514509d970c20252713152512073f2a431e32478923401a
2d333e3d5b8c38211beda90b1a1c4013243421245b39170705223555704b
0000>/TTE1AD8890t00 AddT42Char 
2 2492 71 <00020053fff2021e02cf001b002d00a1bb002b0003000800042b411b0006
002b0016002b0026002b0036002b0046002b0056002b0066002b0076002b
0086002b0096002b00a6002b00b6002b00c6002b000d5d410500d5002b00
e5002b00025d00b800004558b800152f1bb90015000b3e59b800004558b8
000f2f1bb9000f00073e59b800004558b800052f1bb9000500053e59b800
004558b8001b2f1bb9001b00053e59ba0001001b00151112393031253706
07062322263534373e033b013f0127353707030717150727323637363f01
272606070e03151416015820384142321a1e840e232729133c22064e9d0a
6514509e960a2718503e0739253617121e150c0d4789633e3c3f3cb06f0b
16110a8c3b0a1b1a55fe62a90b1a1c40191a53be170704101913353f4724
25260000>/TTE1AD8890t00 AddT42Char 
2 4704 79 <0001005afff2013302cf000b002500b800004558b800052f1bb90005000b
3e59b800004558b8000b2f1bb9000b00053e593031371337273537070307
171507637d064f9c097c064e9c47020e3b0a1b1a55fdf33a0b1a1c00>/TTE1AD8890t00 AddT42Char 
2 6364 85 <00010055fff701d801d70018004000b800004558b800042f1bb900040007
3e59b800004558b8000b2f1bb9000b00073e59b800004558b800182f1bb9
001800053e59ba00060018000411123930313f012735370f013e01373633
321617072e0123220e020f019115519e0d1f19371e3c2c0b1f111a0e2111
194341350c45eaad0b1a1b588a3150203e08094c080c406c8b4b1200>/TTE1AD8890t00 AddT42Char 
1 10112 36 <0002000f0000029d02a800130016004000b800004558b800032f1bb90003
00093e59b800004558b800082f1bb9000800053e59b800004558b800122f
1bb9001200053e59bb00140001000d00042b30313f020133131f01152335
372f01230f01171523010b010f4f4001252a511748f14e0711d73f2f4ad8
01cd28951b10650218fde865101b1b1065787865101b012d0112feee>/TTE1AD8890t00 AddT42Char 
1 14204 51 <0002002300000248028e001700230099bb000c0003001d00042b410500da
001d00ea001d00025d411b0009001d0019001d0029001d0039001d004900
1d0059001d0069001d0079001d0089001d0099001d00a9001d00b9001d00
c9001d000d5db8000c10b80025dc00b800004558b800062f1bb900060009
3e59b800004558b800162f1bb9001600053e59bb00180001001100042bb8
000610b900040001fcb8001fd0b80020d030313f021337273521321e0215
140e022b010f0117152101323e0235342b01031e0123551f490a50011533
3c200a153b6a543921095bfef101242742301a7d3e350e1a1b1065016f65
101a1b282f1516474432a465101b0157192d3d246bfef50304000000>/TTE1AD8890t00 AddT42Char 
1 12612 44 <000100230000019e028e000f002500b800004558b800062f1bb900060009
3e59b800004558b8000e2f1bb9000e00053e5930313f021337273521150f
01030717152123551f490a500104561e4a094ffefd1b1065016f65101a1a
1065fe9165101b000000>/TTE1AD8890t00 AddT42Char 

1 0 0 <> /TTE1AD8890t00 AddT42Char 
/TTE1AD8890t00 findfont /CharStrings get begin
/J 45 def
/S 54 def
/R 53 def
/space 3 def
/two 21 def
/one 20 def
/C 38 def
/o 82 def
/n 81 def
/t 87 def
/e 72 def
/H 43 def
/a 68 def
/d 71 def
/l 79 def
/r 85 def
/A 36 def
/P 51 def
/I 44 def
/.notdef 0 def
end
/TTE1AD8890t00 findfont /Encoding get
dup 1 /J put
dup 2 /S put
dup 3 /R put
dup 4 /space put
dup 5 /two put
dup 6 /one put
dup 7 /C put
dup 8 /o put
dup 9 /n put
dup 10 /t put
dup 11 /e put
dup 12 /H put
dup 13 /a put
dup 14 /d put
dup 15 /l put
dup 16 /r put
dup 17 /A put
dup 18 /P put
dup 19 /I put
pop
F /F4 0 /0 F /TTE1AD8890t00 mF 
/F4S41 F4 [65 0 0 -65 0 0 ] mFS
F4S41 Ji 
1539 2048 M <01020304050606040708090A0B090A040C0D090E0F0B1004111213>[21 33 38 13 33 33 33 13 42 27 33 18 24 33 18 14 48 32 32 32 17 24 26 13 44 34  0]xS 
6408 VM?
1 2548 11 <00010027ff43012302bc001d005bbb00140004000700042b411b00060014
001600140026001400360014004600140056001400660014007600140086
00140096001400a6001400b6001400c60014000d5d410500d5001400e500
1400025d00b8000c2fb800002f3031052e0327263534373e0137170e0307
06151416171e031707010a2b4132240f1212186257191f32271e0b100808
091825352619bd223f465334454a4b435991441a203b404a2f414d264622
28424043271b0000>/TTE153FD90t00 AddT42Char 
1 10276 38 <00010030fff00257029e0025019fbb00160004000500042b411b00060016
001600160026001600360016004600160056001600660016007600160086
00160096001600a6001600b6001600c60016000d5d410500d5001600e500
1600025d00b800004558b8000a2f1bb9000a00093e59b800004558b80000
2f1bb9000000053e59b8000a10b900130001fc410500d9001300e9001300
027141210008001300180013002800130038001300480013005800130068
001300780013008800130098001300a8001300b8001300c8001300d80013
00e8001300f8001300105d411b0008001300180013002800130038001300
480013005800130068001300780013008800130098001300a8001300b800
1300c80013000d71b8000010b9001b0001fc41210007001b0017001b0027
001b0037001b0047001b0057001b0067001b0077001b0087001b0097001b
00a7001b00b7001b00c7001b00d7001b00e7001b00f7001b00105d411b00
07001b0017001b0027001b0037001b0047001b0057001b0067001b007700
1b0087001b0097001b00a7001b00b7001b00c7001b000d71410500d6001b
00e6001b000271303105222e0235343e023332161f0123272e0123220615
141e023332363f0133070e030178547b5128285380582d583b0b2c171d48
2a78832344613d21501e162c0a182d313910335b804c467c5c36090d9472
090c998a467856310e0e6b95040806030000>/TTE153FD90t00 AddT42Char 
1 12056 43 <00010022000002bf028e00230081b800242fb800252fb8002410b80002d0
b800022fb9001f0004fcb8000ad0b8002510b80014dcb9000d0004fcb800
1cd000b800004558b800062f1bb9000600093e59b800004558b800102f1b
b9001000093e59b800004558b800182f1bb9001800053e59b800004558b8
00222f1bb9002200053e59bb000c0001001d00042b30313f02112f013521
150f011521352f013521150f01111f011521353f013521151f0115212253
0a0a530102510a01500b510102520b0b52fefe510bfeb00a51fefe1b1065
016f65101a1a10659d9d65101a1a1065fe9165101b1b1065abab65101b00
>/TTE153FD90t00 AddT42Char 
1 9684 36 <000200080000029c029c00130016004000b800004558b800032f1bb90003
00093e59b800004558b800082f1bb9000800053e59b800004558b800122f
1bb9001200053e59bb00140001000d00042b30313f021333131f01152335
372f01230f01171523010b0108502cbe2cb82b4bf54b1a29d62c194eda01
9a5d5f1b1065020cfdf465101b1b1065757565101b012b010cfef400>/TTE153FD90t00 AddT42Char 
1 13988 51 <00020022000001e5028e0017002400bfb800252fb800262fb8002510b800
02d0b800022fb8002610b8000cdcb8000210b900130004fcb8000c10b900
1b0004fc410500da001b00ea001b00025d411b0009001b0019001b002900
1b0039001b0049001b0059001b0069001b0079001b0089001b0099001b00
a9001b00b9001b00c9001b000d5db8001310b8001fd000b800004558b800
062f1bb9000600093e59b800004558b800162f1bb9001600053e59bb0018
0001001100042bb8000610b900040001fcb8001ed0b8001fd030313f0211
2f013521321e0215140e022b01151f0115211316363734262b01111e0232
22530a0a5301192c402a141c3c6044200a5dfef2de4752014851380a0c0a
0d1b1065016f65101a1829392126463620a165101b0156014d453a47fef4
02020200>/TTE153FD90t00 AddT42Char 
1 12296 44 <0001002200000126028e000f002fbb000b0004000200042b00b800004558
b800062f1bb9000600093e59b800004558b8000e2f1bb9000e00053e5930
313f02112f013521150f01111f01152122530a0a530104530a0a53fefc1b
1065016f65101a1a1065fe9165101b000000>/TTE153FD90t00 AddT42Char 
1 2736 12 <00010013ff43010e02bc001e0063bb00190004000800042b410500da0008
00ea000800025d411b000900080019000800290008003900080049000800
5900080069000800790008008900080099000800a9000800b9000800c900
08000d5db8001910b80020dc00b800112fb8001e2f3031173e03373e0135
3426272e0327371e03171e011514070e0107132031271d0b080908080919
253426192b4032240f0a0812186157a3203b404a2f224726254622284240
43271b223f4652352346254b44589244>/TTE153FD90t00 AddT42Char 
2 6128 79 <0001001a0000010c02ca000d002fbb00090003000200042b00b800004558
b800072f1bb90007000b3e59b800004558b8000c2f1bb9000c00053e5930
313f02112f01353707111f01152325480b0b53aa0a0b47e71b0c4b01d44c
0c1b1184fe2c4b0c1b00>/TTE153FD90t00 AddT42Char 
/TTE153FD90t00 findfont /CharStrings get begin
/parenleft 11 def
/C 38 def
/H 43 def
/A 36 def
/P 51 def
/I 44 def
/parenright 12 def
/l 79 def
end
/TTE153FD90t00 findfont /Encoding get
dup 29 /parenleft put
dup 30 /C put
dup 31 /H put
dup 32 /A put
dup 33 /P put
dup 34 /I put
dup 35 /parenright put
dup 36 /l put
pop
F3S41 Ji 
2289 2048 M <051D1E1F2021222305072424181004050708082403060709031811040507110C051809020A0B05>[13 20 42 48 44 34 21 20 14 30 19 19 34 50 25 13 31 35 36 19 20 29 30 21 20 34 37 25 14 30 37 35
13 34 21 38 30 24  0]xS 
30000 VM?
Pscript_WinNT_Compat begin /$x mysetup def end
Pscript_WinNT_Incr dup /suspend get exec
Pscript_WinNT_Compat dup /initialize get exec
%%BeginDocument: Pscript_Win_PassThrough
1338 1982/G1379073 FmND

%%EndDocument

Pscript_WinNT_Compat dup /suspend get exec
Pscript_WinNT_Incr dup /resume get exec
/DeviceRGB dup setcolorspace /colspABC exch def
0 0 0 1 scol 1602 VM?
2 4036 73 <0001001e0000016c02d0002100cfbb001d0003000200042bb8000210b800
06d0b8001d10b80018d0b800182f00b800004558b8000c2f1bb9000c000b
3e59b800004558b800052f1bb9000500073e59b800004558b800192f1bb9
001900073e59b800004558b800202f1bb9002000053e59b8001910b90003
0001fcb80004d0b8000c10b900130002fc410500d9001300e9001300025d
411b00080013001800130028001300380013004800130058001300680013
00780013008800130098001300a8001300b8001300c80013000d5db80004
10b8001bd0b8001cd030313f021123353f013e0333321617072e0123220e
021f01331523111f01152320470b545401011b2f412516240e1c12271514
21160a020576760b53f21b0c4b01281f104e2a45301a080642080a091625
1c7325fed84b0c1b>/TTE153FD90t00 AddT42Char 
2 10412 91 <0001001c000001e401bf0027008300b800004558b800072f1bb900070007
3e59b800004558b800102f1bb9001000073e59b800004558b8001d2f1bb9
001d00053e59b800004558b800262f1bb9002600053e59ba000a001d0007
111239ba000c001d0007111239ba000e001d0007111239ba0020001d0007
111239ba0022001d0007111239ba0024001d000711123930313f032f0235
3315071f013f01273533150f0106070e01071f02152335372f010f011715
231c353e4c493d33cb382b2a2b2d36a73c3a120e0c1301573b35cd382937
352d35a61b0c4b6e6c4c0c1b1b0c4c3e3e4d0b1b1b0c4c1914111c017f4b
0c1b1b0c4b52524b0c1b0000>/TTE153FD90t00 AddT42Char 
/TTE153FD90t00 findfont /CharStrings get begin
/f 73 def
/x 91 def
end
/TTE153FD90t00 findfont /Encoding get
dup 37 /f put
dup 38 /x put
pop
1344 2128 M <04030E0324070B050A11090309030A0405100309020507052403250A060F06240A0509180507060905070405061811090A1109050207110C240A0B04190522110509020A05061811090A260905182505>
[25 19 56 20 19 30 24 13 30 38 21 20 21 20 29 25 14 49 20 21 38 13 31 13 19 19 21 29 29 33 29 19
30 13 22 34 13 30 29 21 14 30 25 13 29 34 37 22 30 37 22 13 38 30 38 34 19 30 24 25 14 13 21 38
13 22 37 30 13 29 34 37 22 30 33 22 13 34 20  0]xS 
2403 VM?
1 3200 15 <00010026ff7400a4005d000f0063bb000c0004000600042b410500da0006
00ea000600025d411b000900060019000600290006003900060049000600
5900060069000600790006008900060099000600a9000600b9000600c900
06000d5db8000c10b80011dc00b8000f2fb800092f3031173e0127062635
343633321615140607262626081c1f1b1a1d23353c6b0a2f23011e191620
302a314f0f00>/TTE153FD90t00 AddT42Char 
2 2032 69 <00020007ffeb01de02ca0018002a019db8002b2fb8002c2fb8002b10b800
05d0b800052fb900270003fcb8000bd0b8002c10b80014dcba000c000500
14111239b9001e0003fc410500da001e00ea001e00025d411b0009001e00
19001e0029001e0039001e0049001e0059001e0069001e0079001e008900
1e0099001e00a9001e00b9001e00c9001e000d5d00b800004558b8000a2f
1bb9000a000b3e59b800004558b8000f2f1bb9000f00073e59b800004558
b800002f1bb9000000053e59ba000c0000000a111239b900190001fc4121
000700190017001900270019003700190047001900570019006700190077
0019008700190097001900a7001900b7001900c7001900d7001900e70019
00f7001900105d411b000700190017001900270019003700190047001900
5700190067001900770019008700190097001900a7001900b7001900c700
19000d71410500d6001900e60019000271b8000f10b900230002fc410500
d9002300e9002300025d411b000800230018002300280023003800230048
0023005800230068002300780023008800230098002300a8002300b80023
00c80023000d5d303117222e0227112f01353707153e0133321e0215140e
0227323e0235342e022322060715171e01dc0e191c21150a52a90a1d512d
22392a18224260232b3d2713111e2a1922411e0a141e150104080802464c
0c1b1184c3262b1e384d2f3b664b2b26243c4f2b27412f1a1f25e6550606
>/TTE153FD90t00 AddT42Char 
2 4344 74 <0003002bfefd01d301d8003a004600590212bb00570003000500042bbb00
210004003e00042bbb00360003004c00042bba000a00050036111239411b
000600570016005700260057003600570046005700560057006600570076
0057008600570096005700a6005700b6005700c60057000d5d410500d500
5700e5005700025dba001300050057111239b800132fb8000dd0b8000d2f
ba001000050036111239410500da003e00ea003e00025d411b0009003e00
19003e0029003e0039003e0049003e0059003e0069003e0079003e008900
3e0099003e00a9003e00b9003e00c9003e000d5dba001e003e0021111239
b8001310b900440004fcb8002cd0410500da004c00ea004c00025d411b00
09004c0019004c0029004c0039004c0049004c0059004c0069004c007900
4c0089004c0099004c00a9004c00b9004c00c9004c000d5db8003610b800
5bdc00b800004558b800182f1bb9001800073e59b800004558b8001c2f1b
b9001c00073e59bb00470001000000042bbb003b0001002600042bba0010
0026003b111239b8001810b900410001fc410500d9004100e90041000271
412100080041001800410028004100380041004800410058004100680041
00780041008800410098004100a8004100b8004100c8004100d8004100e8
004100f8004100105d411b00080041001800410028004100380041004800
41005800410068004100780041008800410098004100a8004100b8004100
c80041000d71303113222e0235343e02372e01353436372e0135343e0233
3216173715231e0115140e02232226270e0115141e02171e0315140e0203
323635342623220615141613323e0235342e022f010e03151416e1274331
1b121d2615262d2e282b31172d41291d311d7e671414192d3e2609190d16
2215232e1826483822213d56272d3233332f35364721392a180a1d342b5e
0c1b180f52fefd111f2d1d1426211b0908231b1d2d16115136223b2c190c
1321391a2f26223c2d1b03040c1f0e0d110c0904060b182c27243d2d1a01
b042393e4f453f3e46fe7c0f1924150f18141108100714191e102d360000
>/TTE153FD90t00 AddT42Char 
/TTE153FD90t00 findfont /CharStrings get begin
/comma 15 def
/b 69 def
/g 74 def
end
/TTE153FD90t00 findfont /Encoding get
dup 39 /comma put
dup 40 /b put
dup 41 /g put
pop
1344 2208 M <15131603270509020A040A050A11090309030A0405070B0A051513160305280D110C240A04190501020A0525182424181003112905040D28040A060903181104050C0A2503110A05031105>
[46 32 45 20 13 13 22 38 29 25 30 13 30 37 22 19 22 19 30 25 13 30 25 29 13 46 33 45 19 13 34 38
37 35 19 29 26 13 13 38 38 30 13 20 34 19 20 34 49 20 37 32 13 26 37 34 26 29 29 22 19 34 37 26
13 34 29 21 19 38 29 13 20 37  0]xS 
801 VM?
1 4428 21 <00010038000001b4028e001c00d7bb00110003000300042b410500da0003
00ea000300025d411b000900030019000300290003003900030049000300
5900030069000300790003008900030099000300a9000300b9000300c900
03000d5db8001110b8001edc00b800004558b8000c2f1bb9000c00093e59
b800004558b8001b2f1bb9001b00053e59b8000c10b900060002fc410500
d9000600e9000600025d411b000800060018000600280006003800060048
0006005800060068000600780006008800060098000600a8000600b80006
00c80006000d5db8001b10b900160002fc3031373e01353426232207273e
0133321e0215140e0207333f013307213d8e904139523f18205f3f2d4129
1423466846af4910220bfe942771de583f5049122d3b1d303e212d676d6f
35104592>/TTE153FD90t00 AddT42Char 
/TTE153FD90t00 findfont /CharStrings get begin
/two 21 def
end
/TTE153FD90t00 findfont /Encoding get
dup 42 /two put
pop
1344 2288 M <0E180B0A050C0A090703240502181005121314052A1B1B051E1811090A1109051F07110C240A0B052021220506071105280A05030E08240A0E0A11090A0C051811051513160305>
[56 34 24 29 12 35 29 22 30 19 20 12 38 34 49 13 20 32 39 12 33 33 32 13 42 33 38 22 29 38 22 12
48 30 38 34 19 30 24 12 45 33 21 12 29 30 38 12 34 30 12 19 56 36 20 29 56 30 37 22 30 34 12 34
37 12 46 33 45 19  0]xS 
1344 2369 M <07110C0502181005280D110C240A04050607110507060905070405061811090A1109050207110C240A0B0419>[30 38 34 13 38 34 49 14 34 37 38 34 19 30 25 13 29 30 38 13 30 29 22 13 30 25 13 29 34 38 21 30
38 21 13 38 31 37 34 20 29 24 26  0]xS 
/F1S57 F1 [87 0 0 -87 0 0 ] mFS
F1S57 Ji 
819 2524 M <0110011011>[43 23 43 23  0]xS 
626 VM?
1 5068 36 <0002000bfff00236029a000e0013004a401901141440150b11130f0b0413
0f070e000807020c0300010446762f3718003f3c3f3c2f3cfd3c012e2e2e
2e002e3130014968b900040014496861b0405258381137b90014ffc03859
370f02273f0113331f0113072f040f01b816120f7616149d9b0f11a9810f
2921330e0e34bc503e3e203e3e020e3e3efdf4223e8e70b13e3eb100>/TTE16B5380t00 AddT42Char 
1 7468 51 <00020031000001c2029a000f0019005a4022011a1a401b070c19100f0d0c
0302001504071918070312060a0403020f0000010046762f3718003f3c3f
3c2ffd10fd3c012ffd2e2e2e2e2e2e2e2e002e3130014968b90000001a49
6861b0405258381137b9001affc03859333f010333321615140623222715
1f0103163332363534262b0131040206bb6274796412240204061c102e3c
352f323e3e021e6c61637c04763e3e0163043b302c35>/TTE16B5380t00 AddT42Char 
/TTE16B5380t00 findfont /CharStrings get begin
/A 36 def
/P 51 def
end
/TTE16B5380t00 findfont /Encoding get
dup 24 /A put
dup 25 /P put
pop
1344 2524 M <140E070809070805150B071216090A05181906>[50 45 45 33 43 45 33 20 57 42 45 46 22 43 31 20 53 44  0]xS 
30000 VM?
Pscript_WinNT_Compat begin /$x mysetup def end
Pscript_WinNT_Incr dup /suspend get exec
Pscript_WinNT_Compat dup /initialize get exec
%%BeginDocument: Pscript_Win_PassThrough
813 2436/G1379074 FmND

%%EndDocument

Pscript_WinNT_Compat dup /suspend get exec
Pscript_WinNT_Incr dup /resume get exec
/DeviceRGB dup setcolorspace /colspABC exch def
0 0 0 1 scol 2403 VM?
2 5920 78 <000200170000020a02ca000d00210065bb00090003000200042b00b80000
4558b800072f1bb90007000b3e59b800004558b800172f1bb9001700073e
59b800004558b8000c2f1bb9000c00053e59b800004558b800202f1bb900
2000053e59ba000f000c0007111239ba0015000c000711123930313f0211
2f01353707111f01152325372f023f02273533150f021f02152321480b0b
52a90a0b39d9011d323c2c48491b4240c941594e86512ccc1b0c4b01d44c
0c1b1184fe2c4b0c1b1b0c4b3347451b4c0c1b1b0c4c4c8e4b0c1b00>/TTE153FD90t00 AddT42Char 
1 12852 48 <0001002200000350028e0022009cbb001e0003000200042bbb0010000400
1700042bba000900020010111239b8001010b80024dc00b800004558b800
062f1bb9000600093e59b800004558b8000b2f1bb9000b00093e59b80000
4558b800132f1bb9001300053e59b800004558b8001a2f1bb9001a00053e
59b800004558b800212f1bb9002100053e59ba000900130006111239ba00
1800130006111239ba001d0013000611123930313f02112f013533171b01
3733150f01111f011521353f01110307232703111f01152322530a0a53c5
30a0a62fc4520b0b52fefd520bb6333434b30b53e21b1065016f65101a8f
fe6001a28d1a1065fe9165101b1b106501c8fe38909001c8fe3865101b00
0000>/TTE153FD90t00 AddT42Char 
1 11100 40 <0001002200000206028e001f007abb00170004000200042bb8001710b800
0cd000b800004558b800102f1bb9001000073e59b800004558b800062f1b
b9000600093e59b800004558b8001e2f1bb9001e00053e59bb000e000100
1500042bb8001e10b900010001fcb8000610b900040001fcb8000bd0b800
0cd0b8000110b80019d0b8001ad030313f02112f01352117232f01231133
3f013315232f0123151f01333f0133072122530a0a5301c80b27155e9251
510a21210a51510a514e5716270bfe271b1065016f65101aa66f10fefc0b
52e1530aac591010639a>/TTE153FD90t00 AddT42Char 
/TTE153FD90t00 findfont /CharStrings get begin
/k 78 def
/M 48 def
/E 40 def
end
/TTE153FD90t00 findfont /Encoding get
dup 43 /k put
dup 44 /M put
dup 45 /E put
pop
F3S41 Ji 
1344 2648 M <01020A051E1811090A1109051F07110C240A0B05202122050304050711051808090318110724050807062B07290A0525180B0509020A05122A2C2D050824070925180B0E1905220905>
[38 38 29 14 41 34 38 22 29 38 22 13 48 30 38 34 19 30 24 13 44 34 21 13 20 25 13 30 38 13 34 35
22 20 33 38 30 20 13 35 31 28 36 30 32 30 13 20 34 24 13 22 38 30 13 20 33 58 36 13 36 19 30 22
21 33 25 56 13 13 21 22  0]xS 
30000 VM?
Pscript_WinNT_Compat begin /$x mysetup def end
Pscript_WinNT_Incr dup /suspend get exec
Pscript_WinNT_Compat dup /initialize get exec
%%BeginDocument: Pscript_Win_PassThrough
1338 2582/G1379075 FmND

%%EndDocument

Pscript_WinNT_Compat dup /suspend get exec
Pscript_WinNT_Incr dup /resume get exec
/DeviceRGB dup setcolorspace /colspABC exch def
0 0 0 1 scol 801 VM?
1 3356 16 <0001001700d700e301000003000d00bb00010001000200042b3031133315
2317cccc010029000000>/TTE153FD90t00 AddT42Char 
/TTE153FD90t00 findfont /CharStrings get begin
/hyphen 16 def
end
/TTE153FD90t00 findfont /Encoding get
dup 46 /hyphen put
pop
1344 2728 M <072424181004050708082403060709031811040507110C051809020A0B050A11090309030A04050918050B0A290304090A0B0509020A0E040A24170A0405070405061811090A1109050207112E>
[30 19 20 34 49 25 14 30 36 35 20 19 29 30 22 19 34 38 25 13 30 38 34 13 34 22 38 29 24 13 30 37
22 20 21 20 29 25 14 21 34 13 24 30 32 19 25 22 30 24 13 22 38 29 56 25 30 19 33 30 25 13 30 25
14 28 34 38 22 29 38 22 13 38 30 37  0]xS 
1344 2808 M <0C240A0B040507110C05091805031117182B0A051809020A0B05061811090A1109050207110C240A0B0419052005061811090A1109050207110C240A0B050304050711050A110903090F05>
[34 19 30 24 25 13 30 38 34 13 22 34 13 19 38 33 34 35 30 13 34 22 38 29 24 13 29 34 38 21 30 38
21 13 38 31 37 34 20 29 24 26 13 13 44 13 29 34 38 21 30 38 21 13 38 31 37 34 20 29 24 13 20 25
13 30 38 13 30 37 22 19 22 33  0]xS 
1344 2888 M <100309020507052403250A060F06240A051D090F0803060724240F05071105070808240306070903181123050902070905020704050B0A290304090A0B0A0C050309040A242505091805280A05>
[50 19 22 38 13 30 13 19 20 20 30 28 33 29 19 30 13 20 22 33 36 19 29 30 20 19 33 13 30 38 13 30
36 36 19 19 29 30 22 20 33 38 20 13 22 38 30 22 13 38 30 26 13 24 29 32 20 25 22 29 24 30 34 13
19 22 25 30 19 21 13 22 33 14 34 29  0]xS 
1344 2968 M <031117182B0A0C0509020B180D29020509020A05202122190501020A050B0A290304090A0B0A0C050A11090309030A0405090F0803060724240F050207110C240A0504180E0A050618112E>
[19 38 33 34 35 30 34 13 22 38 24 34 37 32 38 13 22 38 29 13 45 33 21 14 13 38 38 29 14 24 29 32
20 25 21 30 24 30 34 13 29 38 22 19 22 19 30 25 13 22 33 36 19 29 30 19 20 33 13 38 30 37 35 19
30 13 25 34 56 29 13 29 34 38  0]xS 
801 VM?
2 7948 84 <0002002eff1101f801d40019002b018ab8002c2fb8002d2fb80015dcb900
020003fcb8002c10b8000bd0b8000b2fb8000210b8001dd0b8000b10b900
270003fc411b000600270016002700260027003600270046002700560027
0066002700760027008600270096002700a6002700b6002700c60027000d
5d410500d5002700e5002700025d00b800182fb800004558b800102f1bb9
001000073e59b800004558b800062f1bb9000600053e59ba000300180010
111239b9001a0002fc411b0007001a0017001a0027001a0037001a004700
1a0057001a0067001a0077001a0087001a0097001a00a7001a00b7001a00
c7001a000d5d410500d6001a00e6001a00025db8001010b900220001fc41
0500d9002200e90022000271412100080022001800220028002200380022
00480022005800220068002200780022008800220098002200a8002200b8
002200c8002200d8002200e8002200f8002200105d411b00080022001800
220028002200380022004800220058002200680022007800220088002200
98002200a8002200b8002200c80022000d713031053f01350e0123222e02
35343e023332161707111f0115230332363735272e0123220e0215141e02
0115470b204733233b2a1724425e3a2244220a0b43e32e20461a0b121e18
2b3e2612101e2bd70f4bb8262a1f3950303f654726090c73fe374b0d1a01
112320e754050722394d2a2844311b000000>/TTE153FD90t00 AddT42Char 
/TTE153FD90t00 findfont /CharStrings get begin
/q 84 def
end
/TTE153FD90t00 findfont /Encoding get
dup 47 /q put
pop
1344 3049 M <090A11092705280D09050902030405030405111809050B0A2F0D030B0A0C190520052403250A060F06240A050A110903090F05060711050B0A290304090A0B050309040A242505>
[22 29 38 22 13 12 35 37 22 12 22 38 19 26 12 19 26 12 38 34 21 13 24 30 33 37 19 24 30 34 13 13
44 13 19 19 21 29 29 33 29 19 30 12 30 38 21 20 21 33 13 29 30 37 13 24 30 32 19 25 22 29 25 12
19 22 25 30 19 21  0]xS 
3071 3049 M <1811240F0525180B0509020A05>[34 38 19 33 13 20 34 24 12 22 38 30  0]xS 
1344 3129 M <080D0B0818040A05182505280A0311290503>[36 37 24 36 34 25 30 13 34 20 13 35 29 19 38 32 13  0]xS 
1831 3129 M <1117182B0A0C05280F051809020A0B050A11090309030A040507110C05070808240306070903181104>[38 33 34 35 30 34 13 34 33 14 33 22 38 30 24 13 29 38 22 19 22 19 30 25 13 30 38 34 13 30 36 36
19 20 28 31 21 20 34 37  0]xS 
2958 3129 M (')S 
2972 3129 M <0510030902180D0905110A0A0C2E>[13 49 20 22 38 33 38 22 13 37 30 29 35  0]xS 
1602 VM?
2 0 56 <00010016ffe902b4028e002100e3bb000c0004000300042bbb001d000300
1400042bb8001d10b80023dc00b800004558b800072f1bb9000700093e59
b800004558b800182f1bb9001800093e59b800004558b800002f1bb90000
00053e59b9000f0001fc41210007000f0017000f0027000f0037000f0047
000f0057000f0067000f0077000f0087000f0097000f00a7000f00b7000f
00c7000f00d7000f00e7000f00f7000f00105d411b0007000f0017000f00
27000f0037000f0047000f0057000f0067000f0077000f0087000f009700
0f00a7000f00b7000f00c7000f000d71410500d6000f00e6000f00027130
3105222635112f013521150f0111141633323e0235112f013533150f0111
140e02015e707c0a520103530a5c562845331e0a53e5510b22405d176d72
013765101a1a1065fec5565f15314f3a012165101a1a1065feec45623e1d
0000>/TTE153FD90t00 AddT42Char 
1 12720 47 <00010022000001fe028e0013003dbb000b0004000200042b00b800004558
b800062f1bb9000600093e59b800004558b800122f1bb9001200053e59b9
00010001fcb8000dd0b8000ed030313f02112f013521150f01111f01333f
0133072122530a0a530104530a0a51475715270afe2e1b1065016f65101a
1a1065fe91591010639a0000>/TTE153FD90t00 AddT42Char 
/TTE153FD90t00 findfont /CharStrings get begin
/U 56 def
/L 47 def
end
/TTE153FD90t00 findfont /Encoding get
dup 48 /U put
dup 49 /L put
pop
1344 3209 M <031129050918050207110C240A0507110F05061811090A1109190522111718060709031811040506071105280A052807040A0C05181105061811090A11090530143127050618112E>
[19 38 32 13 22 34 13 38 30 37 35 19 29 14 30 37 33 13 29 34 38 21 30 38 21 14 13 21 38 33 34 29
30 21 20 34 37 25 14 28 31 37 13 35 29 13 35 30 25 30 34 13 34 37 13 29 34 38 21 30 38 21 14 46
39 34 13 13 29 34 37  0]xS 
1344 3289 M <090A110905090F080A>[22 29 38 22 12 22 33 35  0]xS 
1587 3289 M (')S 
801 VM?
1 10804 39 <0002002200000293028e0012002200bbb800232fb800242fb8002310b800
02d0b800022fb8002410b8000cdcb900180004fc410500da001800ea0018
00025d411b00090018001900180029001800390018004900180059001800
69001800790018008900180099001800a9001800b9001800c90018000d5d
b8000210b9001f0004fc00b800004558b800062f1bb9000600093e59b800
004558b800112f1bb9001100053e59b900010001fcb8000610b900040001
fcb8000110b80013d0b8000410b8001dd0b8001ed030313f02112f013521
321e0215140e02232125323e0235342e022b0111171e0122530a0a53014b
5170461f2a57875cfef301253f5f4021183e6b5468081e3e1b1065016f65
101a2d4f6b3f4e846036272d506d4036654d2ffe285a08070000>/TTE153FD90t00 AddT42Char 
/TTE153FD90t00 findfont /CharStrings get begin
/D 39 def
end
/TTE153FD90t00 findfont /Encoding get
dup 50 /D put
pop
1600 3289 M <05180B05061811090A1109050207110C240A0B0522321905200B290D0E0A110904050E070F050724041805280A05080704040A0C050918050705061811090A110905>
[13 33 25 12 29 34 37 22 30 37 22 12 38 30 38 34 20 29 24 13 21 46 14 12 44 24 32 38 56 29 38 22
25 12 56 30 33 13 30 19 26 33 13 34 30 12 36 30 25 25 30 34 12 22 34 12 31 12 29 34 37 22 30 37
22  0]xS 
1344 3370 M <0207110C240A0B>[38 30 38 34 19 30  0]xS 
1557 3370 M (')S 
1570 3370 M <0507110C0509020A05061811090A1109050207110C240A0B05060711050B0A090D0B11050B0A040D2409040507110C050705040907090D0419>[13 30 38 34 13 22 38 30 13 28 34 38 22 29 38 22 13 38 30 37 35 19 30 24 13 29 30 37 13 25 29 22
37 25 37 13 24 30 25 38 19 22 25 13 30 38 34 13 30 13 26 21 30 22 38 25  0]xS 
1344 3486 M <01020A051E1F2021220504080A06032503060709031811050618110403040904051825050705290A110A0B03060508070B090507110C0507050824070925180B0E>
[38 38 29 14 41 49 44 33 22 13 25 36 29 29 19 21 19 29 30 22 19 34 38 13 29 34 37 25 20 25 22 25
13 34 20 13 31 13 32 29 38 29 24 20 29 13 35 31 24 22 13 30 38 34 13 30 13 36 19 30 22 21 34 24
 0]xS 
3108 3486 M <2E04>[16  0]xS 
3149 3486 M <080A060325030605>[36 30 28 20 20 19 29  0]xS 
30000 VM?
Pscript_WinNT_Compat begin /$x mysetup def end
Pscript_WinNT_Incr dup /suspend get exec
Pscript_WinNT_Compat dup /initialize get exec
%%BeginDocument: Pscript_Win_PassThrough
1338 3421/G1379076 FmND

%%EndDocument

Pscript_WinNT_Compat dup /suspend get exec
Pscript_WinNT_Incr dup /resume get exec
/DeviceRGB dup setcolorspace /colspABC exch def
0 0 0 1 scol 1344 3566 M <08070B09190501020A05290A110A0B03060508070B090518250509020A0504080A060325030607090318110507080824030A0405091805072424051E1F20212205030E08240A0E0A1109072E>
[36 30 24 22 13 13 38 38 30 13 32 29 38 30 24 19 29 13 36 30 24 22 13 34 20 13 22 38 30 13 25 36
29 29 19 21 19 29 30 22 19 34 38 13 30 36 36 19 19 30 25 13 22 34 13 30 19 20 13 42 48 44 34 21
13 19 56 36 20 29 56 30 37 22 30  0]xS 
1344 3647 M <0903181104270510020A0B0A07040509020A050824070925180B0E>[22 19 34 37 26 13 13 50 38 29 24 30 30 25 13 22 38 30 13 36 19 30 22 20 34 24  0]xS 
2091 3647 M <2E04>[17  0]xS 
2133 3647 M <080A06032503060508070B0905110A0A0C0405091805280A0504080A060325030A0C0525180B050A070602051207170705>[36 29 29 19 21 19 29 13 36 30 24 22 13 37 30 30 34 25 13 22 34 13 34 30 13 25 36 29 29 19 21 19
30 34 13 20 34 24 14 29 30 29 38 13 21 30 33 30  0]xS 
1344 3727 M <0824070925180B0E050902070905020704050309040518101105061811060A0809040525180B050708082403060709031811050807062B07290311290507110C052403250A>
[36 19 30 22 20 34 24 56 13 22 38 30 22 13 38 30 26 13 19 22 25 13 34 50 37 13 29 34 38 28 30 36
21 26 13 20 34 24 13 31 35 36 19 20 29 30 22 19 34 37 13 36 30 29 36 30 32 19 38 32 13 30 38 34
13 19 20 20  0]xS 
3213 3727 M <06>S 
3241 3727 M <0F06240A05>[33 29 19 30  0]xS 
1344 3807 M <080B030E030903170A04190501020A051E1F202122050C0A2503110A040509020A050824070925180B0E>[36 24 19 56 19 22 19 33 30 25 13 11 38 38 30 11 42 48 44 34 21 11 34 30 20 20 37 30 25 11 22 38
29 11 36 19 30 22 21 33 25  0]xS 
2517 3807 M <2E04>[16  0]xS 
2558 3807 M <080A06032503060508070B090518250509020A0504080A0603250306070903181105>[36 29 29 20 20 19 29 11 36 30 24 22 11 34 20 11 22 38 29 11 26 35 30 29 19 20 20 29 30 21 20 34
37  0]xS 
1344 3887 M <25180B052C182803240A05221125180B0E070903181105320A1703060A05210B182503240A051D2C223221232705280D0905240A07170A04050309050D110C0A2503110A0C0525180B05>
[20 34 24 13 58 34 34 20 19 30 13 21 38 20 34 24 56 30 22 20 33 38 13 46 30 33 19 29 30 13 33 24
34 20 20 19 30 13 20 58 21 47 33 20 14 13 34 37 22 13 20 29 30 33 30 25 13 20 21 13 38 38 34 29
21 19 38 29 34 13 21 34 24  0]xS 
1344 3968 M <1809020A0B050824070925180B0E0419051E1811040A2F0D0A1109240F2705070504080A0603250306070903181105>[34 21 38 30 24 13 36 19 31 21 21 34 24 56 25 13 13 42 34 38 25 29 33 38 29 38 22 19 33 13 14 30
13 25 36 29 29 19 21 19 29 30 22 19 34 38  0]xS 
2610 3968 M <030405110A0A0C0A0C05>[19 25 14 37 30 29 34 30 34  0]xS 
2875 3968 M <25180B0509020A050824070925180B0E05>[21 34 24 13 22 37 30 13 36 19 30 22 21 34 24 56  0]xS 
1344 4048 M <04080A06032503060508070B0904051825051E1F2021220518110509020A0515131603050824070925180B0E>[25 36 29 29 19 21 19 29 13 36 30 24 22 25 13 34 21 13 42 48 44 33 22 13 34 38 13 21 38 30 13 46
32 45 20 13 36 19 30 22 20 34 24  0]xS 
2568 4048 M <19>S 
1344 4164 M <01020A0525182424181003112905040D28040A06090318110405280B030A25240F050C0A2503110A0509020A050824070925180B0E>[38 38 29 14 20 34 19 20 33 50 19 38 32 13 25 38 34 25 30 29 21 20 34 37 25 13 35 24 19 30 20 20
33 13 34 29 21 19 38 29 13 22 38 30 13 36 19 30 22 20 34 24  0]xS 
2793 4164 M <2E04>[17  0]xS 
2835 4164 M <080A06032503060508070B0904051825050705>[36 29 29 19 21 19 29 13 36 30 24 22 25 13 34 20 14 30  0]xS 
30000 VM?
Pscript_WinNT_Compat begin /$x mysetup def end
Pscript_WinNT_Incr dup /suspend get exec
Pscript_WinNT_Compat dup /initialize get exec
%%BeginDocument: Pscript_Win_PassThrough
1338 4099/G1379077 FmND

%%EndDocument

Pscript_WinNT_Compat dup /suspend get exec
Pscript_WinNT_Incr dup /resume get exec
/DeviceRGB dup setcolorspace /colspABC exch def
0 0 0 1 scol 1344 4245 M <1E1F20212205030E08240A0E0A110907090318110518110509020A0515131603050824070925180B0E1905>[42 48 44 34 21 11 20 56 35 20 29 56 30 38 21 31 21 20 33 38 11 34 38 11 22 38 30 11 46 32 45 20
11 36 19 30 22 21 33 25 56 13  0]xS 
801 VM?
1 11320 41 <00010022000001ed028e001b0064bb00170004000200042bb8001710b800
0cd000b800004558b800102f1bb9001000073e59b800004558b800062f1b
b9000600093e59b800004558b8001a2f1bb9001a00053e59bb000e000100
1500042bb8000610b900040001fcb8000bd0b8000cd030313f02112f0135
2117232f012311333f013315232f0123151f01152122530a0a5301c00b26
1560894e510b20200b514e0a63feec1b1065016f65101a9a6310fefc0b52
e1530aac65101b00>/TTE153FD90t00 AddT42Char 
/TTE153FD90t00 findfont /CharStrings get begin
/F 41 def
end
/TTE153FD90t00 findfont /Encoding get
dup 51 /F put
pop
2607 4245 M <3307>[33  0]xS 
2670 4245 M <0E032403070B>[56 20 19 19 30  0]xS 
2839 4245 M <03090F>[19 22  0]xS 
2913 4245 M <05100309020509020A051E1811090A110905>[11 50 19 22 38 11 22 38 29 12 42 34 37 22 30 37 22  0]xS 
1344 4325 M <1F07110C240A0B052021220504080A06032503060709031811>[48 30 38 34 19 30 24 13 45 33 21 13 25 36 30 29 19 20 20 28 31 21 20 34  0]xS 
2042 4325 M <050304050704040D0E0A0C>[13 20 25 13 30 25 26 37 56 30  0]xS 
2351 4325 M <19050102>[13 13 38  0]xS 
2453 4325 M <03040504>[19 26 13  0]xS 
2536 4325 M <080A0603250306070903181105>[36 29 29 19 21 19 29 30 22 19 34 38  0]xS 
2874 4325 M ($)S 
2893 4325 M <0304090405>[20 25 21 26  0]xS 
2998 4325 M <1811240F05>[34 37 20 33  0]xS 
3135 4325 M <09020A05070C0C032E>[21 38 30 13 30 34 34 20  0]xS 
1344 4405 M <090318110724050B0A2F0D030B0A0E0A1109040507110C050624070B03250306070903181104050902070905070B0A05110A0A0C0A0C05091805030E08240A0E0A11090509020A0520212205>
[22 19 34 37 31 19 12 24 29 33 38 19 24 30 56 29 38 22 25 11 31 37 34 12 29 19 30 24 20 20 20 28
31 21 20 34 37 25 12 22 38 30 22 11 30 25 29 12 37 30 29 35 29 34 12 22 34 11 20 56 35 20 29 56
30 37 22 12 22 37 30 12 44 33 22  0]xS 
1344 4485 M <0311050711050311090A0B18080A0B0728240A0510070F0518110509020A051513160305130A0B1703060A052124070925180B0E19>[19 38 13 30 38 13 19 38 22 29 24 34 36 29 25 30 34 19 30 13 50 30 33 13 34 38 13 21 38 30 13 46
32 45 20 13 32 30 24 33 19 29 30 13 33 19 30 22 21 34 24 56  0]xS 
; ; LH
(%%[Page: 1]%%) = 
%%PageTrailer

%%EndPageComments
%%BeginPageSetup
/DeviceRGB dup setcolorspace /colspABC exch def
mysetup concat colspRefresh
%%EndPageSetup

30000 VM?
Pscript_WinNT_Compat begin /$x mysetup def end
Pscript_WinNT_Incr dup /suspend get exec
Pscript_WinNT_Compat dup /initialize get exec
%%BeginDocument: Pscript_Win_PassThrough
% Copyright (c) 1986-1995 Frame Technology Corporation.
% DO NOT CHANGE THE FIRST LINE; Adobe PS 5.1.2 depends on it.
/FMcmyk 100 dict def
/FmBD{bind def}bind def
/FmLD{load def}FmBD
/FMc {
 FMcmyk length FMcmyk maxlength ge { /FMcmyk FMcmyk dup length dup add dict copy def } if
 4 array astore 4 1 roll 8 bitshift add 8 bitshift add exch FMcmyk 3 1 roll put
}FmBD
/setcmykcolor where { pop
 /sc where { pop /sc load 0 get /scignore eq {
  /FMsc /sc FmLD
  /sc { 3 copy 8 bitshift add 8 bitshift add FMcmyk 1 index known
   { FMcmyk exch get aload pop setcmykcolor pop pop pop } { pop FMsc } ifelse
  }FmBD
 } if } if
} if
mark { /S load
 dup 0 get /PenW eq { dup 1 get /sl load eq {
  dup 0 { PenW .75 sub sl } bind put 1 /exec load put
 } if } if
} stopped cleartomark       
/FmX matrix defaultmatrix def
/FmDC {transform FmX defaultmatrix itransform cvi exch cvi exch} def
/FmBx { dup 3 index lt {3 1 roll exch} if 
 1 index 4 index lt {4 -1 roll 3 1 roll exch 4 1 roll} if
}FmBD
/FmPD/cleartomark FmLD
/FmPD2/cleartomark FmLD
/FmPD4/cleartomark FmLD
/FmPT/pop FmLD
/FmPA{pop pop pop}FmBD
/FmND{pop pop pop}FmBD
systemdict /pdfmark known systemdict /currentdistillerparams known and {
  /FmPD/pdfmark FmLD
  currentdistillerparams /CoreDistVersion get 2000 ge {
    /FmPD2/pdfmark FmLD
    % FmPD4 is like FmPD but for Acrobat 4.05-specific PDF
    /FmPD4U true def
    currentdistillerparams /CoreDistVersion get 4050 ge 
    {
      /FmPD4 /pdfmark load def
    }
    {
      /FmPD4
      { FmPD4U 
	{(%%[Acrobat Distiller 4.05 and up is required to generate Logical PDF Structure]%%) = 
	  (%%[Logical PDF Structure is not generated.]%%) = flush
	} if
	/FmPD4U false def
	cleartomark
      } FmBD
    } ifelse


% Procedure FmPA used to define named destinations for all paragraphs (and elements). As
% a result, the produced PDF files were very large in size, because they contained numerous
% named destinations that were not actually used. 
% In FrameMaker 5.5.6 FmND procedure was added to allow to distinguish between 
% named destinations that were definitely used and all other named destinations.
% The users were given opportunity to change the definition of the FmPA procedure,
% so that it either directed Distiller to produce or to not produce named destinations
% (under fmCG switch).
% FmND always produced a named destination, but FmPA produced a named destination onlY
% when fmCG switch was set to 'True'.
% FrameMaker 5.5.6 used very simplistic method to determine whether a named destination
% was actually used or not.
% FrameMaker 6.0 and up uses a much more elaborate method to determine whether a 
% named destination is actually used or not. It also has an improved "Acrobat Setup" dialog,
% which allows to specify the user's preference, whether to create PDF files with named
% destinations for all paragraphs, or Wonly those named destinations that are used.
% Therefore, there is no need for fmCG switch in FrameMaker 6.0 and up. Now FmND procedure
% defines a named destination, and FmPA procedure does nothing. Sophisticated users still 
% have ability to modify definition of FmPA if they wish so.

	  /fmCG true def 

	  /FmND
	  { mark exch /Dest exch 5 3 roll /View [ /XYZ 5 -2 roll FmDC null ] /DEST FmPD 
	  }FmBD

	  /FmPA 
	  { fmCG
	    { pop pop pop }
	    { FmND } ifelse
	  } FmBD
 } if
} if
: N : N [/CropBox[469 5266 FmDC 3776 305 FmDC FmBx]/PAGE FmPD
[/Dest/P.6/DEST FmPD2
: N ; 1364 4151/M9.62926.Heading3.3241.ContentHandlergetAppName FmND
: N ; 1364 4151/I1.1379384 FmND
: N ; 1364 2517/M9.80326.Heading2.223.Content.Handler.Access.Control FmND
: N ; 1364 2517/I1.1385694 FmND
: N ; [/Rect[907 5092 1010 5010]/Border[0 0 0]/Dest/M21.9.last.page/Action/GoToR/File(meg/book.pdf)/LNK FmPD2
: N [/Title(A)/Rect[779 4965 3492 633]/ARTICLE FmPD2
; ; : N : N 
%%EndDocument

Pscript_WinNT_Compat dup /suspend get exec
Pscript_WinNT_Incr dup /resume get exec
/DeviceRGB dup setcolorspace /colspABC exch def
0 0 0 1 scol 1269 VM?
1 2850 21 <000100340000018601e30015005e40250116164017141409110a0100040f
151404040f0404131211060007060c0c01150000010a46762f3718003f3c
3f10fd10fd3c3c012ffd10fd3c10fd3c2e2e002e2e3130014968b9000a00
16496861b0405258381137b90016ffc0385933353e013534262322072736
333216151407333f01153e717e332e402e2a4359495bd0663e3e4a3a8038
2c372b353a544b867b020449>/TTE152A528t00 AddT42Char 
1 5530 38 <0001002afff6020502a40019004a401a011a1a401b0a170b180a11040415
06000d060808020000010446762f3718003f3f10fd10fd012ffd2e2e002e
2e3130014968b90004001a496861b0405258381137b9001affc038590522
2726353437363332170726232207061514171633323717060151844f5451
548b5c4f1f424f623f413a3d6b464c1d5d0a575ca29560642a4127484c7d
7c4b4f273f2b>/TTE152A528t00 AddT42Char 
1 6360 43 <0001003a00000226029a0017006c402c01181840190c1715141312100f0e
0c0b090807060403020014130608070c0b0403030217100f030000010046
762f3718003f173c3f173c2f3cfd3c012e2e2e2e2e2e2e2e2e2e2e2e2e2e
2e2e2e2e003130014968b900000018496861b0405258381137b90018ffc0
3859333f0103330f011521352f01330f0113233f013521151f013a040206
5304020152020453040206530402feae02043e3e021e3e3eacac3e3e3e3e
fde23e3eb2b23e3e>/TTE152A528t00 AddT42Char 
1 11066 74 <0003001dff1b01ac021000250031003c006a402c013d3d403e14381c1015
1409053504232904173a04032f040b1e04073206002c060e1a062613000e
01010346762f3718003f2f2f2ffd10fd10fd012ffd2ffd2ffd2ffd2ffd2e
2e2e2e002e2e2e3130014968b90003003d496861b0405258381137b9003d
ffc0385917222635343726353437263534363332173f0217071615140623
22270615141f011615140603323635342623220615141613323635342f01
06151416d150644b333b425e47312b1c1b192d4d1a5e4811172c4339a676
582936352a29353431374973384b42e54940532618333f1c304d4b581515
16173a3423394b59050e202309081774485701cc31282d3231282d32fe75
30283f10082534282e00>/TTE152A528t00 AddT42Char 
/TTE152A528t00 findfont /CharStrings get begin
/two 21 def
/C 38 def
/H 43 def
/g 74 def
end
/TTE152A528t00 findfont /Encoding get
dup 31 /two put
dup 32 /C put
dup 33 /H put
dup 34 /g put
pop
F0S48 Ji 
845 550 M <170211051F1B1B05200F190D06190D05210C191D0B04192205>[18 37 37 18 37 32 32 19 41 39 37 25 36 37 26 18 48 34 37 37 18 18 37 36  0]xS 
2615 550 M <1702110518190D06070C090D040F1912051A060712040F19051B1C16>[18 38 36 17 18 38 25 36 24 34 32 25 18 38 38 29 17 37 35 25 29 18 38 37 17 33 18  0]xS 
30000 VM?
Pscript_WinNT_Compat begin /$x mysetup def end
Pscript_WinNT_Incr dup /suspend get exec
Pscript_WinNT_Compat dup /initialize get exec
%%BeginDocument: Pscript_Win_PassThrough
839 478/G1380106 FmND

%%EndDocument

Pscript_WinNT_Compat dup /suspend get exec
Pscript_WinNT_Incr dup /resume get exec
/DeviceRGB dup setcolorspace /colspABC exch def
0 0 0 1 scol 317 VM?
1 3578 25 <00020033fff601a9028600140021004c401b0122224023110c0c08180511
1f05041506001b060e070000010446762f3718003f2f2ffd10fd012ffd2f
fd2e2e002e3130014968b900040022496861b0405258381137b90022ffc0
385917222726353436371706070607363332161514070627323635342623
220706151416e54f3033afa00a693e431d344a4a5c34365334453c354930
043e0a3e4173a7db1c451430346031665d623e414351453c4b38141a5166
>/TTE152A528t00 AddT42Char 
/TTE152A528t00 findfont /CharStrings get begin
/six 25 def
end
/TTE152A528t00 findfont /Encoding get
dup 35 /six put
pop
F0S41 Ji 
845 5068 M <2315141616>[34 25 32 35  0]xS 
2565 5068 M <010203040502060708040906050A0B0C0D0E0F07100511060B060C12060513>[47 33 43 16 15 34 32 22 30 16 28 33 15 31 16 30 23 20 34 22 51 15 33 32 16 32 31 26 32 16  0]xS 
30000 VM?
Pscript_WinNT_Compat begin /$x mysetup def end
Pscript_WinNT_Incr dup /suspend get exec
Pscript_WinNT_Compat dup /initialize get exec
%%BeginDocument: Pscript_Win_PassThrough
839 5002/G1380113 FmND

%%EndDocument

Pscript_WinNT_Compat dup /suspend get exec
Pscript_WinNT_Incr dup /resume get exec
N 3419 4989 M 3421 4989 I 3421 4985 I 3419 4985 I C 
/DeviceRGB dup setcolorspace /colspABC exch def
0 0 0 1 scol  L N 1362 4989 M 1360 4989 I 1360 4985 I 1362 4985 I C 
 L N 3419 4989 M 3419 4985 I 1362 4985 I 1362 4989 I C 
 L N 1318 4989 M 1320 4989 I 1320 4985 I 1318 4985 I C 
 L N 837 4989 M 835 4989 I 835 4985 I 837 4985 I C 
 L N 1318 4989 M 1318 4985 I 837 4985 I 837 4989 I C 
 L N 3423 613 M 3425 613 I 3425 609 I 3423 609 I C 
 L N 1367 613 M 1365 613 I 1365 609 I 1367 609 I C 
 L N 3423 613 M 3423 609 I 1367 609 I 1367 613 I C 
 L N 1323 613 M 1325 613 I 1325 609 I 1323 609 I C 
 L N 842 613 M 840 613 I 840 609 I 842 609 I C 
 L N 1323 613 M 1323 609 I 842 609 I 842 613 I C 
 L ; /DeviceRGB dup setcolorspace /colspABC exch def
0 0 0 1 scol F1S57 Ji 
845 757 M <0110011001>[43 23 43 23  0]xS 
312 VM?
1 10712 73 <0001000c0000018f02db0017006c402d01181840190c0d1715141312110c
07060504030200151404030307050f060a0a031312060305011700000104
46762f3718003f3c3f173c3f10fd10fd173c012e2e2e2e2e2e2e2e2e2e2e
2e2e2e002e3130014968b900040018496861b0405258381137b90018ffc0
3859333f0135233533353436333217072623221d0133152313174b040245
456958443929252d4d515102043e3ef667325f71196e126a2367fecc3e00
>/TTE16B5380t00 AddT42Char 
/TTE16B5380t00 findfont /CharStrings get begin
/f 73 def
end
/TTE16B5380t00 findfont /Encoding get
dup 26 /f put
pop
1370 757 M <140E070809070805150B071216090A0506120907080D1A0D0C0B080D0E07>[50 45 45 33 43 45 33 20 57 42 45 45 22 44 31 20 23 46 43 45 32 23 27 22 38 42 32 22 46  0]xS 
30000 VM?
Pscript_WinNT_Compat begin /$x mysetup def end
Pscript_WinNT_Incr dup /suspend get exec
Pscript_WinNT_Compat dup /initialize get exec
%%BeginDocument: Pscript_Win_PassThrough
839 670/G1379078 FmND

%%EndDocument

Pscript_WinNT_Compat dup /suspend get exec
Pscript_WinNT_Incr dup /resume get exec
/DeviceRGB dup setcolorspace /colspABC exch def
0 0 0 1 scol F3S41 Ji 
1370 881 M <20051E1811090A1109051F07110C240A0B0520212205030E08240A0E0A110907090318110503110509020A051513160305130A0B1703060A052124070925180B0E050E0D040905>
[44 13 42 34 38 21 30 37 22 13 49 30 37 35 19 30 24 13 44 33 22 13 19 56 36 19 30 56 29 38 22 30
22 19 34 37 14 19 38 13 21 38 30 13 46 32 45 20 13 32 30 24 33 19 29 30 13 33 19 30 22 21 33 25
56 13 56 37 25 22  0]xS 
30000 VM?
Pscript_WinNT_Compat begin /$x mysetup def end
Pscript_WinNT_Incr dup /suspend get exec
Pscript_WinNT_Compat dup /initialize get exec
%%BeginDocument: Pscript_Win_PassThrough
1364 815/G1379079 FmND

%%EndDocument

Pscript_WinNT_Compat dup /suspend get exec
Pscript_WinNT_Incr dup /resume get exec
/DeviceRGB dup setcolorspace /colspABC exch def
0 0 0 1 scol 1370 961 M <072424181005280D110C240A0405091805280A050B0A290304090A0B0A0C05070405061811090A1109050207110C240A0B04>[30 19 20 33 50 13 35 37 38 34 19 30 25 13 22 34 13 34 30 13 24 29 32 20 25 22 29 24 30 34 13 30
26 13 28 34 38 22 29 38 22 13 38 30 37 35 19 30 24  0]xS 
2725 961 M <2705280F050A0309020A0B0504090709030605180B05>[13 13 35 33 13 29 20 21 38 30 24 13 25 22 30 22 19 29 13 34 24  0]xS 
1370 1041 M <0C0F11070E0306050E0A0711040507>[34 33 37 31 56 19 29 13 56 29 31 37 25 13  0]xS 
1844 1041 M <04050C0A2503110A0C05280F0509020A051E1811090A1109051F07110C240A0B0520212219>[25 13 34 30 20 19 38 30 34 13 34 33 13 22 38 30 13 42 33 38 22 29 38 22 13 48 30 38 34 19 30 24
13 45 33 21  0]xS 
1370 1158 M <130907090306050B0A290304090B0709031811050E071103250A040905020A070C0A0B0405070B0A0509020A0504070E0A05070405>[32 22 30 22 19 29 13 24 30 32 19 25 22 24 30 22 19 34 38 13 56 30 38 19 21 29 25 22 13 38 30 30
34 30 24 25 13 30 24 30 13 22 38 29 13 25 31 56 29 13 31 25  0]xS 
2768 1158 M <090218040A05>[22 38 33 26 29  0]xS 
2929 1158 M <0C0A2503110A0C0503110509020A05>[34 30 20 20 37 30 34 13 19 38 13 22 38 29  0]xS 
30000 VM?
Pscript_WinNT_Compat begin /$x mysetup def end
Pscript_WinNT_Incr dup /suspend get exec
Pscript_WinNT_Compat dup /initialize get exec
%%BeginDocument: Pscript_Win_PassThrough
1364 1093/G1379080 FmND

%%EndDocument

Pscript_WinNT_Compat dup /suspend get exec
Pscript_WinNT_Incr dup /resume get exec
/DeviceRGB dup setcolorspace /colspABC exch def
0 0 0 1 scol 1370 1238 M <0807062B07290A050C0A04060B0308090318110518250509020A051E1811090A1109051F07110C240A0B05202122190501020A050C0F11070E0306050B0A290304090B070903181105>
[35 31 28 36 30 32 30 13 34 29 26 28 24 20 36 21 20 34 37 13 34 21 13 21 38 30 13 42 34 38 21 30
37 22 13 49 30 37 35 19 30 24 13 44 33 22 13 13 38 38 30 13 34 33 38 30 56 19 29 13 24 30 32 19
25 22 24 30 22 19 34 38  0]xS 
1370 1319 M <08070B070E0A090A0B0405070B0A050704050C0A2503110A0C0525180B050E0A0902180C05>[35 31 24 30 56 30 21 30 24 25 13 30 25 29 13 30 26 13 34 29 21 19 38 29 35 13 20 34 24 13 56 30
21 38 34 34  0]xS 
9378 VM?
-0.059 -0.289 1.16 0.918
 256 array 0 1 255 {1 index exch /.notdef put} for  /TTE1515CC0t00
Type42DictBegin
[<00010000000a000a000a000a63767420017c6371000000ac000000486670
676d8333c24f000000f400000014676c79666e12a8b7000009c000008fa2
68656164d25a117400000108000000366868656107e5040d000001400000
0024686d7478a342201900000164000003dc6c6f6361004561b400000540
000003e06d61787001d602df00000920000000207072657004f1f4c30000
09400000007f67646972000000000000000000000000fff501e402a402e1
003b004200390040003c001c00c600aa01ee019d017d01d4013700fe01b5
00e701d901b701515a671206d2b86a18f82a61a30e40eed2803a2755a287
0001000d4001002c764520b003254523616818236860442d000100000001
00008ada610e5f0f3cf5000003e800000000b77de71000000000b77de710
ffc2fedb04880398000000030002000100000000000100000398fedb0000
04b1ffc2ff8904880001000000000000000000000000000000f701f4003f
0000000000c8000000c8000000d400300114002801f6001f01f400370346
0029025d003600960028011e003f011e001c01f8002001e8002300c4001e
014f001e00c400280123001801e900290188001601c4003601b3002601d0
001801aa002201d2003501b1003101d4003701d2002e00c4002800c4001a
016f002901db0023016f002901aa001b03e9002e01fa000e01b9003b0227
002b0266003b01a8003b0183003b0252002b025d003b00c2003b00c2000f
01e0003b017b003b02db003b024a003b0296002b0196003b0296002b01ba
003b01cd002301da00130242003501fa000e02fb000e01e4000a01d80011
01ed002501390048011f00170139001c012c0008018cfff9012cfff8019b
002701cc003c0180002201cb002301b3002200fa000f01b1001e01c9003c
00c0002d00c000150195003c00c0003c02cd002301c9002301d7002201cb
002301ca002301160023015f00210124000e01c90039018b000d0277000d
01730011018b000d0178001a017b001500c5003c017b001b014e001d01fa
000e01f2000e0227002b01a8003b024a003b0296002b02420035019b0027
019b0027019b0027019b0027019b0027019b0027017d002201b3002201b3
002201b3002201b3002200c0003500c0ffc200c0ffd200c0ffdb01c90023
01d7002201d7002201d7002201d7002201d7002201c9003901c9003901c9
003901c9003901cd00290133001e018e002201f4003a0163002101070031
01bf000f01fc003c02cf003302cf003302ed001a012c006b012c001102ca
000c0296002b0286000f01d7001101c90039015a0036015a001a02aa0027
01d70022015a001900d400300156001b01f4ffee0218001e0218001e022f
002800c8000301fa000e01fa000e0296002b030e002b0304002201f4001e
03e8001e015d0028015d001e00c4002800c4001e01e80023018b000d01d8
0011023600110137001e0137001e01cd002900c4002800c4001e015d001e
04b1002901fa000e01a8003b01fa000e01a8003b01a8003600c2003600c2
ffd300c2ffdc00c2ffc30296002b0296002b0296002b0242003502420035
02420035012c0008012c000c012c0007012c003501cd0023015f002100e2
00490266fff201cb002301d80011018b000d0196004101cb003c01e80023
019100440191004b0191004e019100440191002c0191002c00000003017b
fffd00c0fff101ed00250178001a01d6000f0286000f016f0029016f002f
02b7000f01aa000f02b4000f02cf003301f3ffe80354003701c10006014e
001d027f000f01f800100123001801aa000f01aa000f03e8000000c0003c
012c000e012c005c012c002b012cffe3012c004c012c0008000000000000
007c0000007c0000007c0000007c0000010a00000192000002a20000039e
000004c80000060c0000066c000006e80000076400000842000008e20000
095a000009c600000a2800000a9a00000b3800000bf200000c9400000d64
00000e5000000f0e00000fc80000107c0000115200001208000012200000
12380000129400001336000013bc00001488000015c4000016a400001782
0000181e000018c40000198800001a3e00001afa00001bc200001c320000
1ca000001d7200001dfc00001f0800001fc2000020660000211e000021de
000022cc000023a00000243c000024ee000025aa000026c4000027d80000
28ae0000298600002a2600002a9800002b3000002b4000002b9600002bec
00002cba00002d7200002e0800002ed400002f92000030500000318e0000
3250000032f6000033900000344c000034ba000035b40000367000003710
000037dc0000389a00003930000039f800003aa800003b6000003c000000
3d0400003e1800003eec00003fa20000405e000040ce0000418a0000419c
000041b4000041cc000042ac000042c4000042dc000042f40000430c0000
4322000043380000434e000043640000437a00004494000044aa000044c0
000044d6000044ec00004502000045180000452e000045440000455a0000
4570000045860000459c000045b2000045c8000045de000045f40000460a
0000462000004636000046fa00004788000048560000492400004a1a0000
4a7c00004b2a00004c3a00004d6e00004e5600004fb60000500c00005096
000051b8000052dc00005420000055700000564e00005714000057ac0000
58e2000059dc00005a9800005b2800005b9800005c8a00005d0400005d7e
00005d9e00005d9e00005db600005dce00005de600005efc000060020000
606e000060da0000618e00006242000062bc000062ce000063aa000063c0
000063d8000064ec00006546000065a0000066aa000066bc000066cc0000
6782000069020000691a000069320000694a000069620000697a00006992
000069aa000069c2000069da000069f200006a0a00006a2200006a3a0000
6a5200006a6a00006ac400006b4a00006bbe00006c5000006c6800006c7e
00006d0800006dde00006ec600006ede00006ef400006fb0000070800000
70d60000717a00007216000072da0000740c0000757e0000771000007710
000077ca0000786a0000788200007898000079ba00007b0400007b900000
7c4800007d7a00007e5c00007f8e0000809c0000813600008518000085c8
000085e20000870000008816000088260000890e000089f000008ccc0000
8d4600008db000008dc200008e5400008ec800008f3200008fa200010000
00f700e30007003d0004000200080040000a000000beffff000300014027
16161515141413131212111110100f0f0e0e0d0d0c0c0b0b0a0a09090808
0303020201010000018db801ff8545684445684445684445684445684445
684445684445684445684445684445684445684445684445684445684445
6844456844456844456844b3050446002bb3070646002bb10404456844b1
06064568440000>
[16109 16241 4423 ] AllocGlyphStorage
]def 
108 44 
PrepFor2015
AddFontInfoBegin
AddFontInfo
/OrigFontType /TrueType def 
/OrigFontName <50726F6475637475734F534769426F6F6B63> def
/OrigFontStyle () def
/FSType 1 def
AddFontInfoEnd
[16#2c 16#13de7714 ] AddXUID
Type42DictEnd
1 8670 53 <0002003bfff101ad029a0013001e008b403f011f1f40200b0b0a0a0b080b
0c0e0d0d0e0a0b080b0c0f0f100e0e0f130503001e141211041005010219
05071607100f1e1d07030c00040302130000010046762f3718003f3c3f3c
3f10fd3c2f3cfd012ffd2f3cfd173c2f3cfd872e08c408fc0ec4872e0ec4
08fc0ec4012e2e003130014968b90000001f496861b0405258381137b900
1fffc03859333f01033332161514060713072f0223151f01031633323635
3427262b013b04020682576b4138a7431e216347020406151c3c4f21243e
393e3e021e5a52406611fed91f3e3eb7a83e3e016604493e331f2100>/TTE1515CC0t00 AddT42Char 
1 11988 72 <00020022fff4019501e50015001e005e4025011f1f40200c131916140d0c
0e0504110700171618070d0e0d07191c070808010000010446762f371800
3f3f10fd2ffd3c10fd3c3c10fd012ffd2e2e2e2e2e002e3130014968b900
04001f496861b0405258381137b9001fffc0385917222726353437363332
17161d01211416333237170603333f013426232206e15434373734515032
35fecd4a39433b274bda683f4040332e440c4044757145423a3e6b244765
2b3633012a02043e4652>/TTE1515CC0t00 AddT42Char 
1 12368 74 <0003001eff1a01a9021100270033003e008b403c013f3f4040163a1e1217
160a061615161708131212132223083a39393a2005083c050431050c2b05
193705253407001c07282e071015001001010446762f3718003f2f2f10fd
2ffd10fd012ffd2ffd2ffd2ffd2ffd872e0ec40efc0ec4872e0ec40efc08
c4012e2e2e2e002e2e2e3130014968b90004003f496861b0405258381137
b9003fffc03859172227263534372635343726353437363332173f021707
161514062322270615141f01161514060332363534262322061514161332
3635342f0106151416d04f30334c333a41302d45312a1d1a192c4e1a5e46
11162f433ca576572a39382b2a3837323a4b7a364f45e624264053281832
3d1e304d4a2d2b151517173536243b4b59050f222409081674485601ca32
2a2f34332a2e34fe72322b43100727362a30>/TTE1515CC0t00 AddT42Char 
1 12880 76 <0002002d0000009302ca000900150059402201161640171313050d050105
0600040203050907080a071010060500010001010d46762f3718003f3c3f
3c2f10fd012f3c3cfd3c3c2f3cfd3c2ffd003130014968b9000d00164968
61b0405258381137b90016ffc0385913330f011317233f01033722263534
363332161514063c48040202044804020220161d1e15161d1e01d93e3efe
e13e3e3e011fc91d16151e1d16151e00>/TTE1515CC0t00 AddT42Char 
1 14640 86 <00010021fff6013e01e30022005f40240123234024201403020b0c081e1d
1d1e0805132019050e05070016071111010000010246762f3718003f3f10
fd10fd012ffd2f3cfd872e0ec40efc0ec4012e002e2e3130014968b90002
0023496861b0405258381137b90023ffc038591722273716333236353426
2f01263534363332170726232206151417161f0116151406a1423e1f333b
242f24321174574944331e2d3a25301013331174550a273a232c251e230c
041b693b4e24381e27221e10130c041b693e5300>/TTE1515CC0t00 AddT42Char 
1 14840 87 <0001000efff6011c026c0015006e40310116164017141314080d0c050504
0f0e0b0a04090507060303021107000e0d040303070508070c0b06030501
0000010446762f3718003f3f173c2f3c10fd173c10fd012f173cfd173c2f
3cfd3c2e2e002e3130014968b900040016496861b0405258381137b90016
ffc038591722351123353335330f011533152311143332371706bf664b4b
4304026c6c2a221c1e2c0a6f01373d933e3e173dfeca3313341c>/TTE1515CC0t00 AddT42Char 
1 14490 85 <000100230000011601e300120054401f01131340140b0c070b0512050011
10050301020e0709090601120000010546762f3718003f3c3f3c10fd012f
3c3cfd3c2ffd2e2e002e2e3130014968b900050013496861b04052583811
37b90013ffc03859333f01352f01371736333217072623220713173c0402
0c13440e284c1a131819193a1402043e3ed73e3e1341420b460b36fed73e
>/TTE1515CC0t00 AddT42Char 
1 15896 92 <0001000dff1f017f01e00012008d403c01131340140f0c0f070300020e08
0e0f11101011020e080e0f1212001111120a0b08060505060a0b08070708
060607070608120e0801040300010746762f3718003f3c3f3c2f10fd0187
2e08c40efc0ec4872e0ec40efc0ec4872e08c408fc0ec4872e0ec408fc0e
c4012e2e2e2e002e3130014968b900070013496861b0405258381137b900
13ffc03859173f02232f0103371f033713170f01035e1a171a0911136f45
1012450e0e67431715b4ce3e3e523e3e0150143e3ed93e3e0155143e3efd
cf00>/TTE1515CC0t00 AddT42Char 
1 2502 17 <00010028fff3009c0066000b0037400f010c0c400d090905030600000103
46762f3718003f2f012ffd003130014968b90003000c496861b040525838
1137b9000cffc0385917222635343633321615140662192122181921220d
2118182221191722>/TTE1515CC0t00 AddT42Char 
1 1644 11 <0001003fff32010302e30011003b401101121240130911090d0504000803
010446762f3718003f2f012ffd2e2e003130014968b900040012496861b0
405258381137b90012ffc038591726272635343736371706070615141716
17d6462a27292b432d41241f21263dce5a887f737e848853215f83716563
798a5100>/TTE1515CC0t00 AddT42Char 
1 1768 12 <0001001cff3200e102e30011003b401101121240130e090105050e000a03
010146762f3718003f2f012ffd2e2e003130014968b900010012496861b0
405258381137b90012ffc038591727363736353427262737161716151407
06482c41241f21263d2c472b272a2bce216082726464798a50215a887f73
7e848800>/TTE1515CC0t00 AddT42Char 
1 0 0 <0002003f000001b602ee0003000700564020010808400902070405010006
0505030205040700070607010201030000010046762f3718003f3c2f3c10
fd3c10fd3c012f3cfd3c2f3cfd3c003130014968b900000008496861b040
5258381137b90008ffc0385933112111253311233f0177fec7fafa02eefd
123f0271>/TTE1515CC0t00 AddT42Char 
/TTE1515CC0t00 findfont /CharStrings get begin
/R 53 def
/e 72 def
/g 74 def
/i 76 def
/s 86 def
/t 87 def
/r 85 def
/y 92 def
/period 17 def
/parenleft 11 def
/parenright 12 def
/.notdef 0 def
end
/TTE1515CC0t00 findfont /Encoding get
dup 1 /R put
dup 2 /e put
dup 3 /g put
dup 4 /i put
dup 5 /s put
dup 6 /t put
dup 7 /r put
dup 8 /y put
dup 9 /period put
dup 10 /parenleft put
dup 11 /parenright put
pop
F /F5 0 /0 F /TTE1515CC0t00 mF 
/F5S41 F5 [65 0 0 -65 0 0 ] mFS
F5S41 Ji 
2391 1319 M <01020304050607080907020304050602070A0B>[34 34 33 18 28 25 23 31 18 24 34 33 18 28 25 33 24 24  0]xS 
F3S41 Ji 
2902 1319 M <1905>[13  0]xS 
2928 1319 M <0102>[38  0]xS 
3004 1319 M <0A05040A0E07110903060405182505>[30 13 25 30 56 30 38 21 20 28 26 13 34 20  0]xS 
1370 1399 M <09020A0506240704040511070E0A0508070B070E0A090A0B0503110528180902050409070903060507110C050C0F11070E0306050B0A290304090B070903181104>
[21 38 30 11 29 19 30 26 25 11 37 31 56 29 11 36 30 25 30 56 29 22 30 24 11 19 38 11 34 34 22 38
11 25 22 30 22 19 29 11 30 38 34 11 34 33 38 30 56 20 28 11 25 29 32 19 26 21 25 30 21 20 34 37
 0]xS 
3140 1399 M <27050218100A170A0B2705>[14 11 38 34 49 30 33 30 24 13  0]xS 
1370 1479 M <03>S 
1389 1479 M <04050C0A2503110A0C0525180B0509020A051513160305130A0B1703060A052124070925180B>[25 13 34 30 20 20 37 30 34 13 21 34 24 13 22 37 30 13 46 32 45 20 13 32 30 24 33 19 29 30 13 33
19 30 22 21 34  0]xS 
2388 1479 M <0E0503110509020A05251824241810031129050E0711110A0B19>[56 13 19 38 13 22 38 29 13 21 34 19 19 34 50 19 38 32 13 56 30 38 37 30 24  0]xS 
801 VM?
2 472 58 <00010008fff003fe028e0022007600b800004558b800042f1bb900040009
3e59b800004558b8000d2f1bb9000d00093e59b800004558b800162f1bb9
001600093e59b800004558b8001c2f1bb9001c00053e59b800004558b800
212f1bb9002100053e59ba0009001c0004111239ba0012001c0004111239
ba001f001c0004111239303137032f0135331507171b012f013533150717
1b0137273533150f01030723270b010723fe7d2c4df44a1a938e2d4df84b
1a95891751e04f2d742e3f316d662e3f90016f65101a1a1065fe5101af65
101a1a1065fe5101af65101a1a1068fe94a0a00134fecca00000>/TTE153FD90t00 AddT42Char 
/TTE153FD90t00 findfont /CharStrings get begin
/W 58 def
end
/TTE153FD90t00 findfont /Encoding get
dup 52 /W put
pop
1370 1596 M (4)S 
1437 1596 M <020A11050711051513160305280D110C240A050304050B0A290304090A0B0A0C050704050705061811090A1109050207110C240A0B27050309050E0D0409050C0A2503110A050705040A0B2E>
[38 30 37 12 30 38 12 46 32 45 20 12 34 37 38 34 19 30 12 19 25 12 24 30 32 19 25 22 30 24 29 35
12 30 25 12 30 12 29 34 37 22 29 38 22 12 38 30 38 34 19 30 24 13 12 19 22 12 56 37 26 21 12 34
30 20 20 37 30 12 30 12 25 30 24  0]xS 
30000 VM?
Pscript_WinNT_Compat begin /$x mysetup def end
Pscript_WinNT_Incr dup /suspend get exec
Pscript_WinNT_Compat dup /initialize get exec
%%BeginDocument: Pscript_Win_PassThrough
1364 1530/G1385919 FmND

%%EndDocument

Pscript_WinNT_Compat dup /suspend get exec
Pscript_WinNT_Incr dup /resume get exec
/DeviceRGB dup setcolorspace /colspABC exch def
0 0 0 1 scol 1370 1676 M <1703060A050902070905020704050705>[33 19 29 29 13 22 38 30 22 13 38 30 26 13 30  0]xS 
1368 VM?
1 15200 89 <0001000d0000017f01e0000d00674029010e0e400f0a070a02080908090a
0d0d000c0c0d0506080202030101020206030903010d0000010246762f37
18003f3c3f3c10fd01872e08c40efc0ec4872e08c408fc0ec4012e2e002e
3130014968b90002000e496861b0405258381137b9000effc03859332703
371f033713170f0103a21283451012450e0e674317156a3e018e143e3ed9
3e3e0155143e3efeb000>/TTE1515CC0t00 AddT42Char 
1 11634 70 <00010022fff4017901e50017004a401a011818401916150a16090f050413
07000c070707010000010446762f3718003f3f10fd10fd012ffd2e2e002e
2e3130014968b900040018496861b0405258381137b90018ffc038591722
27263534363332170726232206151417163332371706e35634376c5b513e
1d384137492324403f331e460c3f42756c8f283a236252553337253a2a00
>/TTE1515CC0t00 AddT42Char 
1 14096 83 <00020023ff2e01a901e3001500200063402901212140220d120705150500
1f1e141304120503010219050d1607101c07091500100009060101054676
2f3718003f3c3f2f3c10fd10fd012ffd2f3c3cfd173c2ffd2e002e2e3130
014968b900050021496861b0405258381137b90021ffc03859173f01112f
0137173633321716151406232227151f0113323635342623220711163c04
020c13440e2f514f31347b5d262d0204513f5544354a282bd23e3e01a93e
3e1341423d406f758c0f5b3e3e01086555555e3efee11000>/TTE1515CC0t00 AddT42Char 
1 11784 71 <00020023fff601a802db00150021006240280122224023121409120d0c1f
050319180a09040b05100f0e1607001b07070d0c0701130000010346762f
3718003f3c3f2f3c10fd10fd012f3c3cfd173c2ffd2e2e2e002e2e313001
4968b900030022496861b0405258381137b90022ffc03859172226353437
36333217352f01330f01111f01072706273237112623220706151416de53
683b3857352b02044904020c12440e2a574b2d2b383b2528400a7d707547
4414903e3e3e3efe273e3e134142413e01101f313454555f>/TTE1515CC0t00 AddT42Char 
/TTE1515CC0t00 findfont /CharStrings get begin
/v 89 def
/c 70 def
/p 83 def
/d 71 def
end
/TTE1515CC0t00 findfont /Encoding get
dup 12 /v put
dup 13 /c put
dup 14 /p put
dup 15 /d put
pop
F5S41 Ji 
1768 1676 M <0502070C040D02090E040F>[28 34 23 32 17 31 34 18 35 18  0]xS 
F3S41 Ji 
2073 1676 M <05040A0B1703060A05080B18080A0B090F0507110C050D040A0509020A051707240D0A05182505090203040521223205070405>[13 25 30 24 33 19 29 30 13 36 24 34 35 30 24 22 33 13 30 38 34 13 38 25 29 13 22 38 30 13 33 30
19 38 29 13 34 21 13 22 38 19 25 13 33 22 46 13 30 26  0]xS 
1370 1756 M <09020A0506240704040511070E0A0508070B070E0A090A0B0503110509020A05061811090A26090518250509020A051E1811090A1109051F07110C240A0B0520212219>
[21 38 30 13 29 19 30 26 25 13 37 31 56 29 13 36 30 24 31 56 29 22 29 25 13 19 38 13 21 38 30 13
29 34 37 22 30 33 22 13 34 20 13 22 38 30 13 42 33 38 22 29 38 22 13 48 30 38 34 20 29 24 13 45
33 21  0]xS 
1370 1873 M <130907090306050B0A290304090B0709031811051825050705040A0B1703060A050704050705061811090A1109050207110C240A0B050E0D040905250703240503250509020A05>
[32 22 30 22 19 29 13 24 30 32 19 25 22 24 30 22 19 34 38 13 34 20 13 31 13 25 29 25 33 19 29 29
13 30 26 13 30 13 29 34 37 22 30 37 22 13 38 30 38 34 19 30 24 13 56 38 25 22 13 20 30 20 19 13
19 21 13 22 38 29  0]xS 
30000 VM?
Pscript_WinNT_Compat begin /$x mysetup def end
Pscript_WinNT_Incr dup /suspend get exec
Pscript_WinNT_Compat dup /initialize get exec
%%BeginDocument: Pscript_Win_PassThrough
1364 1807/G1379082 FmND

%%EndDocument

Pscript_WinNT_Compat dup /suspend get exec
Pscript_WinNT_Incr dup /resume get exec
/DeviceRGB dup setcolorspace /colspABC exch def
0 0 0 1 scol F5S41 Ji 
1370 1953 M <0502070C040D02090E040F>[28 34 23 31 18 31 33 18 36 18  0]xS 
F3S41 Ji 
1675 1953 M <05040A0B1703060A05080B18080A0B090F050304050D110C0A2503110A0C1905320F11070E0306050B0A290304090B0709031811051825050705040A0B1703060A05>
[13 25 30 24 33 19 29 29 14 35 24 34 36 30 24 22 33 13 19 25 13 38 37 35 29 21 19 37 30 34 13 14
46 33 38 30 56 19 29 13 24 30 32 19 25 22 24 30 22 19 34 38 13 34 20 13 31 13 25 29 24 33 20 29
29  0]xS 
1370 2033 M <0704050705061811090A1109050207110C240A0B050E0D040905250703240503250509020A05>[30 25 12 30 12 28 34 38 22 29 38 22 11 38 30 38 34 19 30 24 12 56 37 25 22 12 20 30 20 19 12 19
21 11 22 38 29  0]xS 
F5S41 Ji 
2331 2033 M <0502070C040D02090E040F>[28 34 24 31 18 30 34 18 35 18  0]xS 
F3S41 Ji 
2636 2033 M <05040A0B1703060A05080B18080A0B090F05080704040A0C05031105070405>[12 25 30 24 33 19 29 29 12 36 24 34 36 29 24 22 33 12 36 30 25 25 30 34 11 20 37 12 30 25  0]xS 
1370 2113 M <070508070B070E0A090A0B050C180A0405111809050B0A080B0A040A1109050705040A0B1703060A05090207090502070405280A0A11050B0A290304090A0B0A0C05031109180509020A05>
[30 13 36 30 24 30 56 30 22 29 24 13 35 33 30 25 13 38 34 22 13 24 29 36 24 30 25 29 38 22 13 30
13 25 30 24 33 19 29 30 13 22 38 30 22 13 38 30 25 13 34 30 30 37 13 24 30 32 19 25 22 30 24 29
35 13 19 38 21 34 13 22 38 30  0]xS 
1370 2194 M <040F04090A0E19>[25 33 25 22 29 56  0]xS 
1370 2310 M <01020304050C0A25031103090318110525180B0509020A0506240704040511070E0A0508070B070E0A090A0B05040A0E07110903060405072404180507080824030A04050918050E0A09022E>
[38 38 19 25 13 34 29 21 19 38 19 22 19 34 38 12 21 34 24 12 22 38 29 13 29 19 30 25 26 12 38 30
56 29 13 36 30 24 30 56 30 21 30 24 13 25 29 56 31 37 22 19 29 25 13 30 19 25 34 13 30 36 35 20
19 30 25 12 22 34 12 56 30 22 38  0]xS 
30000 VM?
Pscript_WinNT_Compat begin /$x mysetup def end
Pscript_WinNT_Incr dup /suspend get exec
Pscript_WinNT_Compat dup /initialize get exec
%%BeginDocument: Pscript_Win_PassThrough
1364 2245/G1379083 FmND

%%EndDocument

Pscript_WinNT_Compat dup /suspend get exec
Pscript_WinNT_Incr dup /resume get exec
/DeviceRGB dup setcolorspace /colspABC exch def
0 0 0 1 scol 801 VM?
1 7840 29 <0002002cfff5009c0197000b001700d4bb00090004000300042b411b0006
000900160009002600090036000900460009005600090066000900760009
008600090096000900a6000900b6000900c60009000d5d410500d5000900
e5000900025db8000310b8000fd0b8000910b80015d0b8000910b80019dc
00b800004558b8000c2f1bb9000c00053e59bb00060002000000042bb800
0c10b80012dc411b00070012001700120027001200370012004700120057
00120067001200770012008700120097001200a7001200b7001200c70012
000d5d410500d6001200e6001200025d3031130626353436333216151406
0322263534363332161514066517221f1a15221d1a17221f1a15221d0127
011e1b15231f191623fecf1e1a162320191424000000>/TTE153FD90t00 AddT42Char 
/TTE153FD90t00 findfont /CharStrings get begin
/colon 29 def
end
/TTE153FD90t00 findfont /Encoding get
dup 53 /colon put
pop
1370 2391 M <180C043505>[34 34 25 13  0]xS 
F5S41 Ji 
1489 2391 M <01020304050607080903020601020304050607080A0B>[34 34 34 18 28 24 24 31 18 34 33 25 34 34 33 18 29 24 23 32 24  0]xS 
F3S41 Ji 
2101 2391 M <2705>[13  0]xS 
342 VM?
1 8908 54 <00010023fff601aa02a40026005f40240127274028231703020c0d081f1e
1e1f080516231c051005070019071414020000010246762f3718003f3f10
fd10fd012ffd2f3cfd872e0ec40efc0ec4012e002e2e3130014968b90002
0027496861b0405258381137b90027ffc038591722273716333236353427
262f012e0135343736333217072623220615141f0116171615140706dd67
532451453d50191d490d5e5434385c6550244d453a4d7d0e592a3036380a
3c35344338311c1f17041d56464e33353b36333f355b240419292f4c5134
3700>/TTE1515CC0t00 AddT42Char 
/TTE1515CC0t00 findfont /CharStrings get begin
/S 54 def
end
/TTE1515CC0t00 findfont /Encoding get
dup 16 /S put
pop
F5S41 Ji 
2127 2391 M <0102030405060708090302061002070C02070A0B>[34 34 34 17 29 24 23 32 18 33 34 24 36 34 23 31 34 24 24  0]xS 
F3S41 Ji 
2693 2391 M <0507110C05>[13 30 37 35  0]xS 
684 VM?
1 15016 88 <00010039fff601a601d9001900624027011a1a401b161816111006050908
070503040e0f051413120c0700111006030501170000010546762f371800
3f3c3f173c10fd012f3c3cfd3c2f3cfd3c3c2e2e2e2e2e002e3130014968
b90005001a496861b0405258381137b9001affc03859172226350327330f
011514163332370327330f01151f01072706c83d4c0204490402342a4928
02044904020c12440e350a534a01083e3e3eb9343a3e01273e3e3ed73e3e
13414200>/TTE1515CC0t00 AddT42Char 
1 13748 81 <000100230000019101e3001a00644029011b1b401c0f07051a050010050f
191805030102131112050e0d1607090906011a100f030000010546762f37
18003f173c3f3c10fd012f3cfd3c3c2f3c3cfd3c2ffd2ffd2e002e313001
4968b90005001b496861b0405258381137b9001bffc03859333f01352f01
37173633321716151317233f0135342623220713173c04020c13440e3558
3d2527020449040235294a2802043e3ed73e3e134142282b4afef83e3e3e
ba333a3efed93e00>/TTE1515CC0t00 AddT42Char 
/TTE1515CC0t00 findfont /CharStrings get begin
/u 88 def
/n 81 def
end
/TTE1515CC0t00 findfont /Encoding get
dup 17 /u put
dup 18 /n put
pop
F5S41 Ji 
2821 2391 M <010203040506070809111207020304050602070A0B>[34 34 33 18 28 25 23 31 18 36 35 23 34 34 18 28 24 34 23 24  0]xS 
F3S41 Ji 
3402 2391 M <19>S 
1370 2507 M <01020A05040907090306050B0A290304090B0709031811050709090B03280D090A0405070B0A05082407060A0C05031109180509020A05>[38 38 29 13 26 21 30 22 20 28 13 25 29 32 19 26 21 25 30 21 20 34 37 13 31 21 22 24 20 34 37 22
30 25 13 30 24 30 13 36 19 30 29 30 34 13 19 38 22 34 13 21 38 30  0]xS 
801 VM?
3 372 112 <0001002601b200a4029b000f0070bb000c0004000600042b410500da0006
00ea000600025d411b000900060019000600290006003900060049000600
5900060069000600790006008900060099000600a9000600b9000600c900
06000d5db8000c10b80011dc00b8000f2fb800004558b800092f1bb90009
00093e593031133e0127062635343633321615140607262626081c1f1b1a
1d23353c01d30a2f23021f191620302a314f0f000000>/TTE153FD90t00 AddT42Char 
/TTE153FD90t00 findfont /CharStrings get begin
/quoteright 112 def
end
/TTE153FD90t00 findfont /Encoding get
dup 54 /quoteright put
pop
2775 2507 M <280D110C240A360405>[34 38 37 35 19 30 13 25  0]xS 
3019 2507 M <0E071103250A0409052503240A>[56 30 38 19 21 29 25 22 13 21 19 19  0]xS 
3361 2507 M <19>S 
30000 VM?
Pscript_WinNT_Compat begin /$x mysetup def end
Pscript_WinNT_Incr dup /suspend get exec
Pscript_WinNT_Compat dup /initialize get exec
%%BeginDocument: Pscript_Win_PassThrough
1364 2442/G1379084 FmND

%%EndDocument

Pscript_WinNT_Compat dup /suspend get exec
Pscript_WinNT_Incr dup /resume get exec
/DeviceRGB dup setcolorspace /colspABC exch def
0 0 0 1 scol 312 VM?
1 2940 22 <00010018ff50019c01e40024005f402501252540262216031f170d0c021c
0411081104220c0b070e0d140619050600001901010246762f3718003f2f
10fd10fd2f3cfd3c012ffd3c10fd2e2e2e2e2e002e2e3130014968b90002
0025496861b0405258381137b90025ffc038591722273716333236353426
2b01353332363534262322072736333216151406071e01151406a8484827
31382f3e493d0c0c394c2f274032324f60576e3d33374783b0236c1a2f26
2530702c231e26226133554d3a4f0f08533b586c>/TTE16B5380t00 AddT42Char 
/TTE16B5380t00 findfont /CharStrings get begin
/three 22 def
end
/TTE16B5380t00 findfont /Encoding get
dup 27 /three put
pop
F1S57 Ji 
845 2662 M <011001101B>[43 23 43 23  0]xS 
1370 2662 M <140E070809070805150B071216090A05180C0C090F0F05140E07080A0E16>[50 45 45 33 43 45 33 20 57 42 45 45 22 44 31 20 51 39 38 43 35 35 20 50 46 45 32 31 45  0]xS 
30000 VM?
Pscript_WinNT_Compat begin /$x mysetup def end
Pscript_WinNT_Incr dup /suspend get exec
Pscript_WinNT_Compat dup /initialize get exec
%%BeginDocument: Pscript_Win_PassThrough
839 2575/G1379085 FmND

%%EndDocument

Pscript_WinNT_Compat dup /suspend get exec
Pscript_WinNT_Incr dup /resume get exec
/DeviceRGB dup setcolorspace /colspABC exch def
0 0 0 1 scol F3S41 Ji 
1370 2786 M <01020A051E1811090A1109051F07110C240A0B0520212205031106240D0C0A040507050E0A0602071103040E05072424181003112905061811090A1109050207110C240A0B0405>
[38 38 29 13 42 34 38 22 29 38 22 13 48 30 38 34 19 30 24 13 44 34 21 13 19 38 29 19 38 34 29 25
14 30 13 56 29 29 38 30 38 19 26 55 14 30 19 19 34 50 19 38 32 13 29 34 37 22 30 37 22 13 38 30
38 34 19 30 24 25  0]xS 
30000 VM?
Pscript_WinNT_Compat begin /$x mysetup def end
Pscript_WinNT_Incr dup /suspend get exec
Pscript_WinNT_Compat dup /initialize get exec
%%BeginDocument: Pscript_Win_PassThrough
1364 2721/G1379086 FmND

%%EndDocument

Pscript_WinNT_Compat dup /suspend get exec
Pscript_WinNT_Incr dup /resume get exec
/DeviceRGB dup setcolorspace /colspABC exch def
0 0 0 1 scol 1370 2866 M <0918051808090318110724240F0524030E03090509020A030B050706060A04040328032403090F050918050705080B0A0C0A2503110A0C052403040905182505031117182B0A0B0419050102030405>
[21 34 13 34 36 22 19 34 38 30 19 20 33 13 19 19 56 20 21 13 22 38 30 19 24 13 31 28 29 30 25 25
19 35 19 19 20 21 33 13 22 34 13 30 14 35 24 30 34 30 20 19 38 30 34 13 19 20 25 22 13 33 21 13
19 38 33 34 35 30 24 25 13 14 38 38 19 25  0]xS 
1370 2947 M <0E0A0602071103040E050B0A2F0D030B0A0405090207090509020A05031117182B0A0B0405060711050C0A2503110A0507050D11032F0D0A05030C0A11090325030A0B0525180B05>
[56 29 29 38 30 38 19 25 56 13 24 30 33 37 20 24 29 26 13 21 38 31 21 13 22 38 30 13 19 38 33 34
35 30 24 25 13 29 30 38 13 34 29 21 19 38 29 13 31 13 37 38 19 33 38 29 13 20 34 29 38 22 19 20
20 29 24 14 20 34 24  0]xS 
1370 3027 M <09020A0E040A24170A0419051E1F202122050C0A2503110A040509020709052C2232240A090405060711050C0A2503110A050902030405030C0A11090325030A0B050D040311290509020A05>
[21 38 30 56 25 30 19 33 29 26 13 13 42 48 45 33 21 13 35 29 21 19 37 30 25 13 22 38 30 22 13 58
21 47 19 30 21 25 14 28 31 37 13 34 30 20 20 37 30 13 22 38 19 25 13 20 34 29 38 22 19 21 19 29
25 13 37 25 20 37 32 13 22 38 30  0]xS 
1602 VM?
1 8348 31 <00010072fff0018001d20005002500b800004558b800012f1bb900010007
3e59b800004558b800052f1bb9000500053e5930313f011707170772f01e
d4d41ee1f11ed3d31e000000>/TTE153FD90t00 AddT42Char 
1 8484 33 <00010074fff0018201d20005002500b800004558b800032f1bb900030007
3e59b800004558b800052f1bb9000500053e5930313f012737170774d4d4
1ef0f00ed3d31ef1f1000000>/TTE153FD90t00 AddT42Char 
/TTE153FD90t00 findfont /CharStrings get begin
/less 31 def
/greater 33 def
end
/TTE153FD90t00 findfont /Encoding get
dup 55 /less put
dup 56 /greater put
pop
1370 3107 M <0709090B03280D090A052C2232240A092E3711382E2232270510020A0B0A05371138050B0A250A0B0405091805070504080A0603250306052C2232240A09051003090203110509020A05>
[30 22 21 25 19 34 38 21 30 13 58 21 47 19 30 21 17 32 38 33 16 22 46 13 13 50 38 30 24 29 13 33
38 33 13 24 29 21 29 24 26 13 22 33 13 31 13 25 36 29 29 19 21 19 29 13 58 21 47 19 30 21 13 50
19 22 38 19 38 13 22 38 29  0]xS 
1370 3187 M <2C2232240A0905040D03090A051220141905>[57 22 46 20 29 22 13 25 38 19 22 29 13 21 44 39 13  0]xS 
801 VM?
1 9832 37 <00030022000001fa028e001a0023002f012fbb002c0004000200042bbb00
0c0004001e00042b410500da001e00ea001e00025d411b0009001e001900
1e0029001e0039001e0049001e0059001e0069001e0079001e0089001e00
99001e00a9001e00b9001e00c9001e000d5dba0027001e000c111239b800
272f410500da002700ea002700025d411b00090027001900270029002700
39002700490027005900270069002700790027008900270099002700a900
2700b9002700c90027000d5db900140004fcba000f00020014111239b800
2c10b80022d0b8001410b80031dc00b800004558b800062f1bb900060009
3e59b800004558b800192f1bb9001900053e59bb001b0001002a00042bb8
001910b900010001fcb8000610b900040001fcb8002a10b9000f0001fcb8
000410b80021d0b80022d0b8000110b80024d030313f02112f013521321e
0215140607321e0215140e022b011332363534262b01151332363534262b
0115171e0122500b0b50010930432912433f2a3e28131c3c5c40e4ca5448
3e444054444e4a5a420b11211b1065016f65101a1728351e335614172a39
212b48341d016c46403343fcfeb94e45414db65d07070000>/TTE153FD90t00 AddT42Char 
/TTE153FD90t00 findfont /CharStrings get begin
/B 37 def
end
/TTE153FD90t00 findfont /Encoding get
dup 57 /B put
pop
1855 3187 M <390A06070D040A0509>[36 30 28 31 37 25 30 13  0]xS 
2107 3187 M <020304050709090B03280D090A050304052C223221>[38 19 25 13 30 22 22 24 19 35 37 22 29 14 19 25 13 58 21 47  0]xS 
2672 3187 M <2E04>[16  0]xS 
2714 3187 M <080A06032503062705>[35 30 29 19 20 20 29 13  0]xS 
2922 3187 M <070506>[30 13  0]xS 
2994 3187 M <180B0B0A040818110C03112905>[34 24 24 30 25 36 34 37 34 20 37 32  0]xS 
1370 3267 M <10070F05091805030C0A110903250F051513160305280D110C240A04>[49 31 33 13 21 34 13 20 34 29 38 22 19 21 33 13 46 32 45 19 13 35 37 38 34 19 30  0]xS 
2166 3267 M <05030405110A0A0C0A0C19>[13 20 25 13 37 30 30 34 29 34  0]xS 
1370 3384 M <20040504080A060325030A0C0503110509020A051E1811090A1109051F07110C240A0B052021220504080A0603250306070903181127050705061811090A1109050207110C240A0B0506071105>
[44 25 12 25 36 29 29 19 21 19 30 34 11 20 37 12 22 37 30 12 41 34 38 22 29 38 22 11 48 31 37 34
20 29 24 12 44 34 21 11 26 35 30 29 19 21 19 29 30 22 19 34 37 14 11 31 11 29 34 37 22 30 37 22
12 37 31 37 34 20 29 24 12 29 30 38  0]xS 
30000 VM?
Pscript_WinNT_Compat begin /$x mysetup def end
Pscript_WinNT_Incr dup /suspend get exec
Pscript_WinNT_Compat dup /initialize get exec
%%BeginDocument: Pscript_Win_PassThrough
1364 3319/G1385554 FmND

%%EndDocument

Pscript_WinNT_Compat dup /suspend get exec
Pscript_WinNT_Incr dup /resume get exec
/DeviceRGB dup setcolorspace /colspABC exch def
0 0 0 1 scol 1370 3464 M <0D040A0509020A050709090B03280D090A052C03060B182D0C03090318112E1F07110C240A0B2E3711382E223205091805030C0A110903250F050309040A24250525180B0509020A05080D0B2E>
[37 25 30 13 22 38 29 13 31 21 22 24 20 34 37 22 30 13 58 19 29 24 34 36 34 19 22 19 34 38 16 49
30 37 35 19 30 24 16 33 37 33 17 21 46 13 22 34 13 20 34 29 38 22 19 20 33 13 20 22 25 29 20 20
13 21 34 24 13 21 38 30 13 36 37 24  0]xS 
1370 3545 M <0818040A04051825050706060A040405061811090B1824190520040509020304050709090B03280D090A050304051811240F050708082403060728240A05091805061811090A1109050207112E>
[35 34 26 29 25 13 34 21 13 30 29 29 29 25 25 14 28 34 38 22 24 34 19 13 13 45 25 13 22 38 19 25
13 30 22 22 24 19 35 37 22 30 13 19 25 13 34 38 19 33 13 30 36 36 19 20 28 31 34 19 30 13 22 34
13 28 34 38 22 29 38 22 13 38 30 38  0]xS 
1370 3625 M <0C240A0B042705280D110C240A04050902070905070B0A0511180905061811090A1109050207110C240A0B04050E0D0409050D040A0509020A05390D110C240A2E>
[34 19 30 24 25 13 13 35 37 38 34 19 30 25 13 22 38 30 22 13 30 24 30 13 38 33 22 13 29 34 38 21
30 38 21 13 38 31 37 34 20 29 24 26 13 56 37 25 22 13 38 25 29 13 22 38 30 13 36 37 38 34 19 30
 0]xS 
801 VM?
1 13124 49 <00010022fff202b1028e001a0087b8001b2fb8001c2fb8001b10b80002d0
b800022fb8001c10b80012dcb900090003fcb8000210b900160003fc00b8
00004558b800062f1bb9000600093e59b800004558b8000d2f1bb9000d00
093e59b800004558b800122f1bb9001200053e59b800004558b800192f1b
b9001900053e59ba000900120006111239ba00150012000611123930313f
02112f0135331701112f013533150f0111232701111f01152322530a0a53
aa54010d0b52e1520b3a5dfee90b53e21b1065016f65101a8ffe65019b65
101a1a1065fdf39e01a8fe5865101b000000>/TTE153FD90t00 AddT42Char 
/TTE153FD90t00 findfont /CharStrings get begin
/N 49 def
end
/TTE153FD90t00 findfont /Encoding get
dup 58 /N put
pop
1370 3705 M <130F0E28182403063A070E0A05020A070C0A0B0507040509020A030B05030C0A110903250306070903181119>[32 33 56 34 34 20 19 29 47 30 56 30 13 38 29 31 34 29 24 14 30 25 13 22 38 29 20 24 13 19 34 30
37 22 20 20 19 29 30 22 19 34 38  0]xS 
312 VM?
1 3140 23 <00020016ff5d01e301e600150018006b402b011919401a10171018171615
131211100e0d0b050403020018160f030e07121104030315000b0a010104
46762f3718003f3c2f3c2f173cfd173c012e2e2e2e2e2e2e2e2e2e2e2e2e
2e2e2e002e2e3130014968b900040019496861b0405258381137b90019ff
c03859053f013523353f04330f01153f011523151f01033507010f0402ff
2a295124218d04022f2e5d02047776a33e3e385b3e3e823e3e3e3eed0204
72383e3e0120cdcd>/TTE16B5380t00 AddT42Char 
/TTE16B5380t00 findfont /CharStrings get begin
/four 23 def
end
/TTE16B5380t00 findfont /Encoding get
dup 28 /four put
pop
F1S57 Ji 
845 3860 M <011001101C>[43 23 43 23  0]xS 
312 VM?
1 6948 48 <00010031000002e2029a001b005e4026011c1c401d0c13081b1a1312100f
0e0c0302001a010c0b040303021b1615100f050000010046762f3718003f
173c3f173c3f012e2e2e2e2e2e2e2e2e2e2e002e2e3130014968b9000000
1c496861b0405258381137b9001cffc03859333f0103331f011317371337
330f0113233f01110703232f0103271331040206b011147114158912a704
020684040213966a13166c13063e3e021e3e3efecf4343016f3e3e3efde2
3e3e01633efe5f3e3e01273efe1f>/TTE16B5380t00 AddT42Char 
/TTE16B5380t00 findfont /CharStrings get begin
/M 48 def
end
/TTE16B5380t00 findfont /Encoding get
dup 29 /M put
pop
1370 3860 M <1D>S 
938 VM?
1 11170 75 <0001002e000001bc02d10018005d4024011919401a0e07181612110f0e07
06040302001407090901040303180f0e030000010046762f3718003f173c
3f3c3f10fd012e2e2e2e2e2e2e2e2e2e2e2e002e3130014968b900000019
496861b0405258381137b90019ffc03859333f0103330f01153633321615
1317233f01353423220713172e040206820402354d404a02048204024731
1e02043e3e02553e3ea937534bfef93e3e3ea55429fef23e>/TTE16B5380t00 AddT42Char 
1 5576 39 <000200310000024c029a000c0016005540200117174018080300160d0401
02120408161506030f06000403020c0000010046762f3718003f3c3f3c10
fd10fd3c012ffd2f3cfd3c2e2e003130014968b900000017496861b04052
58381137b90017ffc03859333f0103333217161514070623271633323635
34262b0131040206db9355585a5d974f301a5e776c61523e3e021e535696
99606278067c67607300>/TTE16B5380t00 AddT42Char 
1 12452 83 <00020019ff2e01cb01e70014001f005f402501202040210c11071e1d1412
110503020018040c15070f1b070914000f0009010601010546762f371800
3f3f3f2f3c10fd10fd012ffd2e2e2e2e2e2e2e2e2e002e2e3130014968b9
00050020496861b0405258381137b90020ffc03859173f01112f01371736
333216151406232227151f0113323635342623220715163004020c117d0b
3050505a705c2d27020436303b322a301b21d23e3e01a73e3e1a45417c73
7688105c3e3e012a4e44434826eb0c00>/TTE16B5380t00 AddT42Char 
/TTE16B5380t00 findfont /CharStrings get begin
/h 75 def
/D 39 def
/p 83 def
end
/TTE16B5380t00 findfont /Encoding get
dup 30 /h put
dup 31 /D put
dup 32 /p put
pop
1441 3860 M <09081E0E12051F090F0C0A0D20080D0E070F>[44 32 45 46 46 20 57 43 35 39 31 22 45 32 22 46 45  0]xS 
30000 VM?
Pscript_WinNT_Compat begin /$x mysetup def end
Pscript_WinNT_Incr dup /suspend get exec
Pscript_WinNT_Compat dup /initialize get exec
%%BeginDocument: Pscript_Win_PassThrough
839 3772/G1379089 FmND

%%EndDocument

Pscript_WinNT_Compat dup /suspend get exec
Pscript_WinNT_Incr dup /resume get exec
/DeviceRGB dup setcolorspace /colspABC exch def
0 0 0 1 scol F3S41 Ji 
1370 3984 M <320A2503110309031811040518250504180E0A051E1811090A1109051F07110C240A0B05202122050E0A0902180C04050724041805110A0A0C0509>[46 30 20 19 38 19 22 19 34 38 25 13 34 20 13 25 34 56 30 12 42 34 38 21 30 38 21 13 48 31 37 34
20 29 24 13 45 33 21 13 56 30 21 38 34 34 25 13 30 20 25 34 13 37 30 29 34 13  0]xS 
3040 3984 M (%)S 
3060 3984 M <0D0B09020A0B050624070B0325>[38 24 22 38 29 24 13 29 19 30 25 19  0]xS 
3390 3984 M <032E>[20  0]xS 
30000 VM?
Pscript_WinNT_Compat begin /$x mysetup def end
Pscript_WinNT_Incr dup /suspend get exec
Pscript_WinNT_Compat dup /initialize get exec
%%BeginDocument: Pscript_Win_PassThrough
1364 3918/G1379090 FmND

%%EndDocument

Pscript_WinNT_Compat dup /suspend get exec
Pscript_WinNT_Incr dup /resume get exec
/DeviceRGB dup setcolorspace /colspABC exch def
0 0 0 1 scol 1370 4064 M <0607090318110503>[28 31 21 20 34 37 13  0]xS 
1574 4064 M <110507110515131603>[37 13 31 37 13 46 33 45  0]xS 
1848 4064 M (.\()[16  0]xS 
1899 4064 M <07040A0C051E1811090A1109051F07110C240A0B0520212205030E08240A0E0A110907090318111905390A24181005>[30 25 30 34 13 42 34 37 22 30 37 22 13 49 30 37 34 20 29 24 14 44 33 22 13 19 56 36 19 30 56 29
38 22 30 22 19 34 37 14 13 36 29 19 34 50  0]xS 
3272 4064 M <070B0A05>[30 24 30  0]xS 
1370 4144 M <0C0A04060B030809031811040518>[34 29 26 28 24 20 36 21 20 34 37 25 13  0]xS 
1751 4144 M <250509020A040A050E0A0902180C040507110C050624070B0325030607090318110419>[21 13 22 38 29 25 30 13 56 29 22 38 34 34 25 13 31 37 34 14 28 20 30 24 19 21 19 29 30 22 19 34
38 25  0]xS 
/F1S41 F1 [65 0 0 -65 0 0 ] mFS
F1S41 Ji 
845 4261 M <011001101C1011>[34 18 34 18 36 18  0]xS 
1251 VM?
1 7142 49 <0001003100000236029a00130057402101141440150a120813120d0c0a09
080302000a0904030302130e0d030000010046762f3718003f173c3f173c
012e2e2e2e2e2e2e2e2e2e002e2e3130014968b900000014496861b04052
58381137b90014ffc03859333f0103331f0303330f0113232f0313310402
066b2528b024067f0402066c2728ad25063e3e021e3e3eff3e01b93e3efd
e23e3ef43efe5200>/TTE16B5380t00 AddT42Char 
1 11922 80 <00010019000002c101e700260069402b0127274028120b072624201f1d1c
1a1615131205030200221807090d09010601261d1c131205000001054676
2f3718003f173c3f3f3c10fd3c012e2e2e2e2e2e2e2e2e2e2e2e2e2e2e00
2e2e3130014968b900050027496861b0405258381137b90027ffc0385933
3f01352f0137173633321736333216151317233f0135342322071317233f
01353423220713173004020c117d0b345253263956414b02048104024633
1a020481040245341a02043e3ed53e3e1a45414343534bfef93e3e3ea554
2afef33e3e3ea5542afef33e>/TTE16B5380t00 AddT42Char 
1 1494 11 <00010042ff23013c02dc000d003a4010010e0e400f050d05090402040001
0246762f3718002f2f012ffd2e2e003130014968b90002000e496861b040
5258381137b9000effc038591726111037170607061514171617e5a3a357
42231e1f2440ddc901100118c8435d76675f5f6a7b57>/TTE16B5380t00 AddT42Char 
1 1606 12 <00010017ff23011202dc000d003a4010010e0e400f0c090105040c0a0001
0146762f3718002f2f012ffd2e2e003130014968b90001000e496861b040
5258381137b9000effc0385917273637363534272627371611106f584223
1e1f234158a3dd425d776660606a7a5643c9fef0fee9>/TTE16B5380t00 AddT42Char 
/TTE16B5380t00 findfont /CharStrings get begin
/N 49 def
/m 80 def
/parenleft 11 def
/parenright 12 def
end
/TTE16B5380t00 findfont /Encoding get
dup 33 /N put
dup 34 /m put
dup 35 /parenleft put
dup 36 /parenright put
pop
1370 4261 M <140E0708090708150B071216090A10170908182020210B22092324>[38 36 35 26 33 36 25 45 32 35 36 17 34 21 19 32 34 25 42 35 35 44 32 53 33 26  0]xS 
30000 VM?
Pscript_WinNT_Compat begin /$x mysetup def end
Pscript_WinNT_Incr dup /suspend get exec
Pscript_WinNT_Compat dup /initialize get exec
%%BeginDocument: Pscript_Win_PassThrough
839 4195/G1379091 FmND

%%EndDocument

Pscript_WinNT_Compat dup /suspend get exec
Pscript_WinNT_Incr dup /resume get exec
/DeviceRGB dup setcolorspace /colspABC exch def
0 0 0 1 scol F3S41 Ji 
1370 4378 M <2005280D110C240A05070609031129050704050705061811090A1109050207110C240A0B05>[44 13 34 38 38 34 19 30 13 30 29 21 20 37 32 13 31 25 13 30 13 29 34 38 21 30 37 22 13 38 31 37
34 20 29 24  0]xS 
2377 4378 M <0E0D0409050C>[56 38 25 22 13  0]xS 
2565 4378 M <0A2503110A050705>[30 20 19 38 30 13 30  0]xS 
684 VM?
1 11244 68 <00020027fff6017701e3001a002400624028012525402617190e170f2205
031e1d0903080515141b07000c07111f1e0708071101180000010346762f
3718003f3c3f2f3cfd3c10fd10fd012f3cfd173c2ffd2e2e002e2e313001
4968b900030025496861b0405258381137b90025ffc03859172226353437
363b0135342623220727363332161d011f01072706273237352322061514
16ab3a4a33304b4732283d381b464f41510c12430f2b4d4a254b2e3c270a
4a42442a28282f36223a264b43cf3e3e1341423f40642e27252a>/TTE1515CC0t00 AddT42Char 
1 13498 80 <000100230000029401e30029007b4035012a2a402b130b07291f1e141305
00282704030102211715160412111d1c04222021251a07090d090601291f
1e1413050000010546762f3718003f173c3f3c3c10fd3c012f3c3cfd3cdd
3cfd3c3c10dd3c3cfd3c2e2e2e2e2e2e2e002e2e3130014968b90005002a
496861b0405258381137b9002affc03859333f01352f0137173633321736
33321716151317233f013534262322071317233f0135342623220713173c
04020c13440e3754502639593b2526020448040233284b26020448040233
284b2602043e3ed73e3e1341424646282b4afef83e3e3eba333a3efed93e
3e3eba333a3efed93e00>/TTE1515CC0t00 AddT42Char 
/TTE1515CC0t00 findfont /CharStrings get begin
/a 68 def
/m 80 def
end
/TTE1515CC0t00 findfont /Encoding get
dup 19 /a put
dup 20 /m put
pop
F5S41 Ji 
2758 4378 M <0502070C040D020912131402>[28 34 23 32 17 31 34 18 35 32 52  0]xS 
F3S41 Ji 
3128 4378 M <05080B18080A0B090F05>[13 36 24 34 36 29 24 22 33  0]xS 
30000 VM?
Pscript_WinNT_Compat begin /$x mysetup def end
Pscript_WinNT_Incr dup /suspend get exec
Pscript_WinNT_Compat dup /initialize get exec
%%BeginDocument: Pscript_Win_PassThrough
1364 4312/G1379092 FmND

%%EndDocument

Pscript_WinNT_Compat dup /suspend get exec
Pscript_WinNT_Incr dup /resume get exec
/DeviceRGB dup setcolorspace /colspABC exch def
0 0 0 1 scol 1370 4458 M <09020709051003242405061811090703110509020A05040A0B1703060A0511070E0A19050102030405>[21 38 31 21 13 50 19 20 19 13 29 34 38 21 30 20 37 14 21 38 30 13 25 30 24 33 19 29 29 14 37 30
56 30 13 13 38 38 20 25  0]xS 
2456 4458 M <080B18080A0B090F05>[36 24 34 35 30 24 22 33  0]xS 
2707 4458 M <1003242405280A050D040A0C0507040509020A050B0A090D0B1105>[50 19 19 20 13 34 30 13 37 25 30 34 13 30 26 13 22 37 30 13 24 30 22 37 24 38  0]xS 
1370 4538 M <1707240D0A05250B180E0509020304050E0A0902180C19>[33 30 19 38 29 13 21 24 34 56 13 22 38 19 25 13 56 30 21 38 34 34  0]xS 
2024 4538 M <05>S 
; ; LH
(%%[Page: 2]%%) = 
%%PageTrailer

%%EndPageComments
%%BeginPageSetup
/DeviceRGB dup setcolorspace /colspABC exch def
mysetup concat colspRefresh
%%EndPageSetup

30000 VM?
Pscript_WinNT_Compat begin /$x mysetup def end
Pscript_WinNT_Incr dup /suspend get exec
Pscript_WinNT_Compat dup /initialize get exec
%%BeginDocument: Pscript_Win_PassThrough
% Copyright (c) 1986-1995 Frame Technology Corporation.
% DO NOT CHANGE THE FIRST LINE; Adobe PS 5.1.2 depends on it.
/FMcmyk 100 dict def
/FmBD{bind def}bind def
/FmLD{load def}FmBD
/FMc {
 FMcmyk length FMcmyk maxlength ge { /FMcmyk FMcmyk dup length dup add dict copy def } if
 4 array astore 4 1 roll 8 bitshift add 8 bitshift add exch FMcmyk 3 1 roll put
}FmBD
/setcmykcolor where { pop
 /sc where { pop /sc load 0 get /scignore eq {
  /FMsc /sc FmLD
  /sc { 3 copy 8 bitshift add 8 bitshift add FMcmyk 1 index known
   { FMcmyk exch get aload pop setcmykcolor pop pop pop } { pop FMsc } ifelse
  }FmBD
 } if } if
} if
mark { /S load
 dup 0 get /PenW eq { dup 1 get /sl load eq {
  dup 0 { PenW .75 sub sl } bind put 1 /exec load put
 } if } if
} stopped cleartomark       
/FmX matrix defaultmatrix def
/FmDC {transform FmX defaultmatrix itransform cvi exch cvi exch} def
/FmBx { dup 3 index lt {3 1 roll exch} if 
 1 index 4 index lt {4 -1 roll 3 1 roll exch 4 1 roll} if
}FmBD
/FmPD/cleartomark FmLD
/FmPD2/cleartomark FmLD
/FmPD4/cleartomark FmLD
/FmPT/pop FmLD
/FmPA{pop pop pop}FmBD
/FmND{pop pop pop}FmBD
systemdict /pdfmark known systemdict /currentdistillerparams known and {
  /FmPD/pdfmark FmLD
  currentdistillerparams /CoreDistVersion get 2000 ge {
    /FmPD2/pdfmark FmLD
    % FmPD4 is like FmPD but for Acrobat 4.05-specific PDF
    /FmPD4U true def
    currentdistillerparams /CoreDistVersion get 4050 ge 
    {
      /FmPD4 /pdfmark load def
    }
    {
      /FmPD4
      { FmPD4U 
	{(%%[Acrobat Distiller 4.05 and up is required to generate Logical PDF Structure]%%) = 
	  (%%[Logical PDF Structure is not generated.]%%) = flush
	} if
	/FmPD4U false def
	cleartomark
      } FmBD
    } ifelse


% Procedure FmPA used to define named destinations for all paragraphs (and elements). As
% a result, the produced PDF files were very large in size, because they contained numerous
% named destinations that were not actually used. 
% In FrameMaker 5.5.6 FmND procedure was added to allow to distinguish between 
% named destinations that were definitely used and all other named destinations.
% The users were given opportunity to change the definition of the FmPA procedure,
% so that it either directed Distiller to produce or to not produce named destinations
% (under fmCG switch).
% FmND always produced a named destination, but FmPA produced a named destination onlY
% when fmCG switch was set to 'True'.
% FrameMaker 5.5.6 used very simplistic method to determine whether a named destination
% was actually used or not.
% FrameMaker 6.0 and up uses a much more elaborate method to determine whether a 
% named destination is actually used or not. It also has an improved "Acrobat Setup" dialog,
% which allows to specify the user's preference, whether to create PDF files with named
% destinations for all paragraphs, or Wonly those named destinations that are used.
% Therefore, there is no need for fmCG switch in FrameMaker 6.0 and up. Now FmND procedure
% defines a named destination, and FmPA procedure does nothing. Sophisticated users still 
% have ability to modify definition of FmPA if they wish so.

	  /fmCG true def 

	  /FmND
	  { mark exch /Dest exch 5 3 roll /View [ /XYZ 5 -2 roll FmDC null ] /DEST FmPD 
	  }FmBD

	  /FmPA 
	  { fmCG
	    { pop pop pop }
	    { FmND } ifelse
	  } FmBD
 } if
} if
: N : N [/CropBox[469 5266 FmDC 3776 305 FmDC FmBx]/PAGE FmPD
[/Dest/P.7/DEST FmPD2
: N ; 1338 3320/M9.51915.External.Reference.16.JSR.211.Content.Handler.API FmND
: N ; 1338 3320/I1.1379166 FmND
: N ; 1338 634/M9.14425.Heading3.3242.ContentHandlergetAuthority FmND
: N ; 1338 634/I1.1379424 FmND
: N ; [/Rect[3297 5088 3398 5009]/Border[0 0 0]/Dest/M21.9.last.page/Action/GoToR/File(meg/book.pdf)/LNK FmPD2
: N ; : N ; [/Rect[2633 2214 3400 2132]/Border[0 0 0]/Dest/G1379091/LNK FmPD2
[/Rect[1344 2294 1809 2214]/Border[0 0 0]/Dest/G1379091/LNK FmPD2
: N [/Title(A)/Rect[753 4965 3466 633]/ARTICLE FmPD2
; ; : N : N 
%%EndDocument

Pscript_WinNT_Compat dup /suspend get exec
Pscript_WinNT_Incr dup /resume get exec
/DeviceRGB dup setcolorspace /colspABC exch def
0 0 0 1 scol F0S41 Ji 
819 5068 M <010203040502060708040906050A0B0C0D0E0F07100511060B060C12060513>[47 33 43 16 16 34 32 22 30 16 28 32 17 30 16 31 23 20 34 22 50 17 32 33 16 32 30 27 32 16  0]xS 
317 VM?
1 3758 26 <00010030ff5a018401d9000c00464016010d0d400e090705040a09080704
0100090801010746762f3718003f3c2f012e2e2e2e2e2e002e2e2e313001
4968b90007000d496861b0405258381137b9000dffc0385917273f011323
0f013521150f018c46201aa87c3e3e01541d1ba61e3e3e019f02044c1b3e
3e00>/TTE152A528t00 AddT42Char 
/TTE152A528t00 findfont /CharStrings get begin
/seven 26 def
end
/TTE152A528t00 findfont /Encoding get
dup 36 /seven put
pop
3241 5068 M <2415141616>[31 25 31 35  0]xS 
F0S48 Ji 
819 550 M <1702110518190D06070C090D040F191205051A060712040F19051B1C16>[18 37 37 18 18 37 26 35 25 34 32 25 18 38 37 30 18 18 37 36 24 29 18 38 38 18 32 18  0]xS 
3080 550 M <11>S 
3114 550 M <060E06070619090612>[35 21 36 24 35 37 31 35  0]xS 
N 3400 613 M 3402 613 I 3402 609 I 3400 609 I C 
 L N 1344 613 M 1342 613 I 1342 609 I 1344 609 I C 
 L N 3400 613 M 3400 609 I 1344 609 I 1344 613 I C 
 L N 1300 613 M 1302 613 I 1302 609 I 1300 609 I C 
 L N 819 613 M 817 613 I 817 609 I 819 609 I C 
 L N 1300 613 M 1300 609 I 819 609 I 819 613 I C 
 L N 3400 4989 M 3402 4989 I 3402 4985 I 3400 4985 I C 
 L N 1344 4989 M 1342 4989 I 1342 4985 I 1344 4985 I C 
 L N 3400 4989 M 3400 4985 I 1344 4985 I 1344 4989 I C 
 L N 1300 4989 M 1302 4989 I 1302 4985 I 1300 4985 I C 
 L N 819 4989 M 817 4989 I 817 4985 I 819 4985 I C 
 L N 1300 4989 M 1300 4985 I 819 4985 I 819 4989 I C 
 L ; /DeviceRGB dup setcolorspace /colspABC exch def
0 0 0 1 scol F1S41 Ji 
819 743 M <011001101C1001>[34 18 34 18 36 18  0]xS 
1344 743 M <14>S 
312 VM?
1 13962 92 <00010008ff1901c001e80015004340150116164017120e1209030015110a
01040300010946762f3718003f3c3f3c2f012e2e2e2e002e3130014968b9
00090016496861b0405258381137b90016ffc03859173f02232f04371f03
3f02170f0103681b181a1e1011411617820e10320e0e430d7a1716b6c83e
3e4c3e3ecf3e3e213e3eac3e3eea3e213e3efdce>/TTE16B5380t00 AddT42Char 
/TTE16B5380t00 findfont /CharStrings get begin
/y 92 def
end
/TTE16B5380t00 findfont /Encoding get
dup 37 /y put
pop
1383 743 M <0E0708090708150B071216090A101709081813081E0E0A0D08252324>[36 35 26 34 35 25 45 32 35 36 18 33 21 19 32 34 26 41 35 25 36 35 25 17 26 33 26  0]xS 
30000 VM?
Pscript_WinNT_Compat begin /$x mysetup def end
Pscript_WinNT_Incr dup /suspend get exec
Pscript_WinNT_Compat dup /initialize get exec
%%BeginDocument: Pscript_Win_PassThrough
813 677/G1379094 FmND

%%EndDocument

Pscript_WinNT_Compat dup /suspend get exec
Pscript_WinNT_Incr dup /resume get exec
/DeviceRGB dup setcolorspace /colspABC exch def
0 0 0 1 scol 801 VM?
2 5696 77 <00020011ff1500c302ba000b001b0085bb00090004000300042b410500da
000300ea000300025d411b00090003001900030029000300390003004900
03005900030069000300790003008900030099000300a9000300b9000300
c90003000d5dba001100030009111239b800112fb8000910b80016d0b800
162fb8001110b900180003fc00b8001b2fbb00060002000000042b303113
2226353436333216151406033e0335112f01353707111406078d16201f17
16201e94202615070b53ad0a474e024e1d19171f20171520fce419393a37
1701404c0c1b1184fecf55813000>/TTE153FD90t00 AddT42Char 
/TTE153FD90t00 findfont /CharStrings get begin
/j 77 def
end
/TTE153FD90t00 findfont /Encoding get
dup 59 /j put
pop
F3S41 Ji 
1344 859 M <01020A050B0A090D0B11051707240D0A050E0D040905280A0509020A05040D283B0A06090518250509020A050403291103112905060A0B090325030607090A>
[38 38 29 14 24 29 22 37 25 37 13 33 30 20 37 30 13 56 37 26 21 13 35 29 13 22 38 30 13 25 37 35
17 30 28 22 13 34 21 13 21 38 30 13 25 20 32 37 20 37 32 13 29 30 24 21 20 20 20 28 31 21  0]xS 
3013 859 M (')S 
3026 859 M <0503>[13  0]xS 
3059 859 M <250509>[20 13  0]xS 
3114 859 M <020A05280D110C240A05>[38 29 14 34 37 38 34 19 30  0]xS 
30000 VM?
Pscript_WinNT_Compat begin /$x mysetup def end
Pscript_WinNT_Incr dup /suspend get exec
Pscript_WinNT_Compat dup /initialize get exec
%%BeginDocument: Pscript_Win_PassThrough
1338 794/G1379095 FmND

%%EndDocument

Pscript_WinNT_Compat dup /suspend get exec
Pscript_WinNT_Incr dup /resume get exec
/DeviceRGB dup setcolorspace /colspABC exch def
0 0 0 1 scol 1344 939 M <02070405280A0A1105040329110A0C0507110C0509020A050403291107090D0B0A05170A0B0325030607090318110502070405280A0A1105040D06060A0404250D2419051509020A0B2E>
[38 30 25 13 35 29 30 37 13 26 19 32 38 29 34 13 31 37 34 13 22 38 30 13 25 19 32 38 30 22 37 25
29 13 33 30 24 19 21 19 29 30 22 19 34 38 13 38 30 25 13 34 30 30 37 13 25 38 29 28 30 25 25 21
37 20 13 13 46 22 38 29 24  0]xS 
1344 1020 M <1003040A05>[50 19 25 30  0]xS 
342 VM?
1 13388 79 <0001003c0000008402db0007004840180108084009040704050300050605
01020403070000010046762f3718003f3c2f3c012f3cfd3c2f3cfd3c0031
30014968b900000008496861b0405258381137b90008ffc03859333f0103
330f01133c040206480402063e3e025f3e3efda1>/TTE1515CC0t00 AddT42Char 
/TTE1515CC0t00 findfont /CharStrings get begin
/l 79 def
end
/TTE1515CC0t00 findfont /Encoding get
dup 21 /l put
pop
F5S41 Ji 
1481 1020 M <12111515>[35 35 18  0]xS 
F3S41 Ji 
1587 1020 M <050E0D040905280A050B0A090D0B110A0C19>[13 56 37 25 22 13 35 29 13 24 30 22 37 24 38 30 34  0]xS 
F1S41 Ji 
819 1136 M <011001101C101B>[34 18 34 18 36 18  0]xS 
1344 1136 M <140E0708090708150B071216090A10170908061F2324>[38 36 35 26 34 35 25 45 32 35 36 18 33 21 19 32 34 26 18 44 26  0]xS 
30000 VM?
Pscript_WinNT_Compat begin /$x mysetup def end
Pscript_WinNT_Incr dup /suspend get exec
Pscript_WinNT_Compat dup /initialize get exec
%%BeginDocument: Pscript_Win_PassThrough
813 1071/G1385751 FmND

%%EndDocument

Pscript_WinNT_Compat dup /suspend get exec
Pscript_WinNT_Incr dup /resume get exec
/DeviceRGB dup setcolorspace /colspABC exch def
0 0 0 1 scol F3S41 Ji 
1344 1253 M <3A18050B0A240A170711090506020711290A0419>[47 34 13 24 30 19 30 33 30 38 21 13 29 38 30 38 32 30 25  0]xS 
30000 VM?
Pscript_WinNT_Compat begin /$x mysetup def end
Pscript_WinNT_Incr dup /suspend get exec
Pscript_WinNT_Compat dup /initialize get exec
%%BeginDocument: Pscript_Win_PassThrough
1338 1187/G1385755 FmND

%%EndDocument

Pscript_WinNT_Compat dup /suspend get exec
Pscript_WinNT_Incr dup /resume get exec
/DeviceRGB dup setcolorspace /colspABC exch def
0 0 0 1 scol F1S41 Ji 
819 1370 M <011001101C101C>[34 18 34 18 36 18  0]xS 
312 VM?
1 8494 57 <0001000b0000023502aa000e003d4012010f0f40100b080b030a04020e00
00010346762f3718003f3c3f3c012e2e002e3130014968b90003000f4968
61b0405258381137b9000fffc03859332f0103371f0113173713170f0103
d30f11a8850f10680e0e887a16149c3e3e020c223e3efe983e3e01e4203e
3efdf200>/TTE16B5380t00 AddT42Char 
/TTE16B5380t00 findfont /CharStrings get begin
/V 57 def
end
/TTE16B5380t00 findfont /Encoding get
dup 38 /V put
pop
1344 1370 M <140E0708090708150B071216090A1017090826090A0F0D0E072324>[38 36 35 26 34 35 25 45 32 35 36 18 33 21 19 32 34 26 36 34 25 27 18 36 35 25  0]xS 
30000 VM?
Pscript_WinNT_Compat begin /$x mysetup def end
Pscript_WinNT_Incr dup /suspend get exec
Pscript_WinNT_Compat dup /initialize get exec
%%BeginDocument: Pscript_Win_PassThrough
813 1304/G1385756 FmND

%%EndDocument

Pscript_WinNT_Compat dup /suspend get exec
Pscript_WinNT_Incr dup /resume get exec
/DeviceRGB dup setcolorspace /colspABC exch def
0 0 0 1 scol F3S41 Ji 
1344 1486 M <2005280D110C240A05070609031129050704050705061811090A1109050207110C240A0B050402180D240C050C0A2503110A050705>[44 13 35 37 38 34 19 30 13 30 29 22 19 38 32 13 30 25 13 30 13 29 34 38 21 30 38 21 14 37 31 37
34 20 29 24 14 25 38 34 37 19 35 13 34 29 21 19 38 29 13 31  0]xS 
342 VM?
1 13936 82 <00020022fff401b501e5000f001c00474019011d1d401e0c19050413050c
10070016070808010000010446762f3718003f3f10fd10fd012ffd2ffd00
3130014968b90004001d496861b0405258381137b9001dffc03859172227
2635343736333217161514070627323635342623220615141716ec59373a
35385d59373935385c3b4e48413b4f23260c4043776b44483d40726f484b
3c685652696251573639>/TTE1515CC0t00 AddT42Char 
/TTE1515CC0t00 findfont /CharStrings get begin
/o 82 def
end
/TTE1515CC0t00 findfont /Encoding get
dup 22 /o put
pop
F5S41 Ji 
2780 1486 M <0502070C040D02090C020705041612>[28 34 23 31 18 31 33 18 32 33 24 28 18 36  0]xS 
F3S41 Ji 
3202 1486 M <05080B18082E>[13 36 24 34 36  0]xS 
30000 VM?
Pscript_WinNT_Compat begin /$x mysetup def end
Pscript_WinNT_Incr dup /suspend get exec
Pscript_WinNT_Compat dup /initialize get exec
%%BeginDocument: Pscript_Win_PassThrough
1338 1421/G1379099 FmND

%%EndDocument

Pscript_WinNT_Compat dup /suspend get exec
Pscript_WinNT_Incr dup /resume get exec
/DeviceRGB dup setcolorspace /colspABC exch def
0 0 0 1 scol 1344 1567 M <0A0B090F0509020709051003242405061811090703110509020A05030E08240A0E0A1109070903181105170A0B040318110518250509020A05040A0B1703060A19050102030405>
[29 25 21 33 13 22 38 30 22 13 50 19 19 20 13 29 34 37 22 30 19 38 13 22 38 29 14 19 56 36 19 30
56 29 38 21 31 21 20 34 37 13 33 30 24 25 20 33 38 13 34 20 14 21 38 30 13 25 30 24 33 19 29 29
14 13 38 38 19 25  0]xS 
3232 1567 M <080B18082E>[36 24 34 36  0]xS 
1344 1647 M <0A0B090F05>[29 25 21 33  0]xS 
1464 1647 M <1003242405280A050D040A0C0507040509020A050B0A090D0B11051707240D0A05250B180E0509020304050E0A0902180C190522250509020A05>[50 19 19 20 11 34 30 12 37 25 30 34 12 30 25 12 21 38 30 11 25 29 22 37 24 38 12 33 30 19 38 29
12 20 24 34 56 12 22 38 19 25 12 56 29 22 38 34 34 13 12 21 21 11 22 38 30  0]xS 
F5S41 Ji 
2966 1647 M <0502070C040D02090C020705041612>[28 34 24 31 18 30 34 18 31 34 23 29 17 36  0]xS 
F3S41 Ji 
3389 1647 M <05>S 
1344 1727 M <080B18080A0B090F05030405111809050C0A2503110A0C270509020A05030E08240A0E0A11090709031811050E0D0409050B0A090D0B1105>[36 24 34 35 30 24 22 33 13 19 25 14 37 34 22 13 34 30 20 19 38 30 34 13 13 22 38 29 13 20 56 36
19 29 56 30 38 21 31 21 20 34 37 13 56 38 25 22 13 24 29 22 38 24 37  0]xS 
F5S41 Ji 
2895 1727 M <12111515>[35 35 18  0]xS 
F3S41 Ji 
3001 1727 M <19>S 
312 VM?
1 3328 24 <0001001fff5001a801d90019005e4024011a1a401b1712110f030f0e0213
12050d0c0804170c0b071413050600000e0d01010246762f3718003f3c2f
10fd2f3cfd3c012ffd2f3cfd3c2e2e2e002e2e2e2e3130014968b9000200
1a496861b0405258381137b9001affc0385917222737163332363534262b
011121152f012315333216151406ad48462a2c3d344252435301543e3e64
12657d8bb023721b302a2a3301587c040276665b6379>/TTE16B5380t00 AddT42Char 
/TTE16B5380t00 findfont /CharStrings get begin
/five 24 def
end
/TTE16B5380t00 findfont /Encoding get
dup 39 /five put
pop
F1S41 Ji 
819 1844 M <011001101C1027>[34 18 34 18 36 18  0]xS 
626 VM?
1 13498 89 <00010008000001c001e80011003d401201121240130e0a0e050d06011100
00010546762f3718003f3c3f3c012e2e002e3130014968b9000500124968
61b0405258381137b90012ffc03859332f04371f033f02170f0103981012
411617820e10320e0e430d7a1716653e3ecf3e3e213e3eac3e3eea3e213e
3efeb500>/TTE16B5380t00 AddT42Char 
1 11648 78 <0001002effee01bd02d10016005940210117174018101307161413100d0b
07060403020011000a01040303160000010046762f3718003f3c3f3c3f3f
012e2e2e2e2e2e2e2e2e2e2e2e002e2e3130014968b900000017496861b0
405258381137b90017ffc03859333f0103330f01113f02170f011f02072f
01151f012e040206820402512422742c69462a2d74475802043e3e02553e
3efeb1723a392e3a925c3a3934737a5f3e3e>/TTE16B5380t00 AddT42Char 
/TTE16B5380t00 findfont /CharStrings get begin
/v 89 def
/k 78 def
end
/TTE16B5380t00 findfont /Encoding get
dup 40 /v put
dup 41 /k put
pop
1344 1844 M <0607280E0C0B080D0E0710170908061F2324050B0712050607280E0C0B080D0E07101709080607280E290D0717061F2324>[18 35 32 35 31 32 26 17 36 35 18 33 34 25 19 44 25 26 16 33 35 35 17 18 35 32 35 30 33 25 18 36
35 18 32 34 26 18 35 32 36 33 17 35 34 18 44 26  0]xS 
30000 VM?
Pscript_WinNT_Compat begin /$x mysetup def end
Pscript_WinNT_Incr dup /suspend get exec
Pscript_WinNT_Compat dup /initialize get exec
%%BeginDocument: Pscript_Win_PassThrough
813 1778/G1385778 FmND

%%EndDocument

Pscript_WinNT_Compat dup /suspend get exec
Pscript_WinNT_Incr dup /resume get exec
/DeviceRGB dup setcolorspace /colspABC exch def
0 0 0 1 scol F3S41 Ji 
1344 1960 M <3A18050B0A240A170711090506020711290A0419>[47 34 13 24 30 19 30 33 30 38 21 13 29 38 30 38 32 30 25  0]xS 
30000 VM?
Pscript_WinNT_Compat begin /$x mysetup def end
Pscript_WinNT_Incr dup /suspend get exec
Pscript_WinNT_Compat dup /initialize get exec
%%BeginDocument: Pscript_Win_PassThrough
1338 1895/G1385782 FmND

%%EndDocument

Pscript_WinNT_Compat dup /suspend get exec
Pscript_WinNT_Incr dup /resume get exec
/DeviceRGB dup setcolorspace /colspABC exch def
0 0 0 1 scol 312 VM?
1 3500 25 <00020029fff501cc02b60012001f004c401b0120204021100a0a081d0404
16041019070c130600070000010446762f3718003f2f10fd2ffd012ffd2f
fd2e2e002e3130014968b900040020496861b0405258381137b90020ffc0
385917222726353436371706073633321716151406273236353426232207
06151416f25a3639b8a824c0332d3f4a2d2f785a26302c263124052e0b42
467aacea296c2e942433365e6d83733f362e36221713404d>/TTE16B5380t00 AddT42Char 
/TTE16B5380t00 findfont /CharStrings get begin
/six 25 def
end
/TTE16B5380t00 findfont /Encoding get
dup 42 /six put
pop
F1S41 Ji 
819 2077 M <011001101C102A>[34 18 34 18 36 18  0]xS 
1344 2077 M <0607280E0C0B080D0E07101709080607280E290D0717182020210B22092324>[18 35 32 35 31 32 26 17 36 35 18 33 34 25 19 34 32 36 33 18 35 33 41 35 36 43 33 52 34 25  0]xS 
30000 VM?
Pscript_WinNT_Compat begin /$x mysetup def end
Pscript_WinNT_Incr dup /suspend get exec
Pscript_WinNT_Compat dup /initialize get exec
%%BeginDocument: Pscript_Win_PassThrough
813 2011/G1385783 FmND

%%EndDocument

Pscript_WinNT_Compat dup /suspend get exec
Pscript_WinNT_Incr dup /resume get exec
/DeviceRGB dup setcolorspace /colspABC exch def
0 0 0 1 scol F3S41 Ji 
1344 2194 M <140A090D0B11051707240D0A050304051828090703110A0C0503110509020A0504070E0A0510070F0507040525180B05>[38 30 22 37 24 38 13 33 30 20 37 30 13 19 25 13 34 35 21 30 20 37 30 34 13 20 37 13 22 38 30 13
25 30 56 30 13 49 31 33 13 30 25 13 21 33 25  0]xS 
2997 VM?
1 3656 17 <0001002ffff100aa006d000b00b6bb00090003000300042b410500da0003
00ea000300025d411b000900030019000300290003003900030049000300
5900030069000300790003008900030099000300a9000300b9000300c900
03000d5db8000910b8000ddc00b800004558b800002f1bb9000000053e59
b80006dc411b000700060017000600270006003700060047000600570006
0067000600770006008700060097000600a7000600b7000600c70006000d
5d410500d6000600e6000600025d30311722263534363332161514066717
21261e1720260f1e1a1a2a1e1a1a2a000000>/TTE1AD8890t00 AddT42Char 
2 3548 74 <00020014ff0d01e601c9002700370089bb00360003001a00042b411b0006
003600160036002600360036003600460036005600360066003600760036
008600360096003600a6003600b6003600c60036000d5d410500d5003600
e5003600025d00b800004558b800202f1bb9002000073e59b800004558b8
00172f1bb9001700053e59bb00070002000000042bba0012001700201112
39303117222627371e0133323e02373e033f010e01070623222635343637
3e013b010f0106070e01031636373e013f01272606070e011514a42b4421
27203c2a232f1e0f030204040503241c3b2140351a1f424328573a553523
0f3f1f4d1c0e25162b412108391a3c172433f31415361716131f29171024
24220d98304f203e3f3c5b8c38211bedf1693b1d1d0125011d172c7f6617
07020d172182524a>/TTE1AD8890t00 AddT42Char 
2 5820 83 <00020023ff1701f001d70018002b008bbb000d0003002100042b410500da
002100ea002100025d411b00090021001900210029002100390021004900
21005900210069002100790021008900210099002100a9002100b9002100
c90021000d5db8000d10b8002ddc00b800182fb800004558b800042f1bb9
000400073e59b800004558b8000a2f1bb9000a00073e59ba000600180004
11123930313f012735370f0136373633321615140e02070e010f03131636
373e033534272206070e010f01179514509d0b1f314543331a1e11213120
235039242b4fc21a3c151321190e1f0b19192a4b250737edaa0b1a1b5589
613f3e413a28514b421b1d1b0201d5140120030a121033424e2a4a010d16
278c6f160600>/TTE1AD8890t00 AddT42Char 
1 3620 16 <0001004700cf010a00fd0003000d00bb00010001000200042b3031373315
2347c3c3fd2e>/TTE1AD8890t00 AddT42Char 
/TTE1AD8890t00 findfont /CharStrings get begin
/period 17 def
/g 74 def
/p 83 def
/hyphen 16 def
end
/TTE1AD8890t00 findfont /Encoding get
dup 20 /period put
dup 21 /g put
dup 22 /p put
dup 23 /hyphen put
pop
F4S41 Ji 
2633 2194 M <0708090A0B090A0C0D090E0F0B1014150B0A11161617>[42 27 33 18 24 33 18 48 32 33 32 17 24 25 14 30 24 19 44 32 31  0]xS 
30000 VM?
Pscript_WinNT_Compat begin /$x mysetup def end
Pscript_WinNT_Incr dup /suspend get exec
Pscript_WinNT_Compat dup /initialize get exec
%%BeginDocument: Pscript_Win_PassThrough
1338 2128/G1379459 FmND

%%EndDocument

Pscript_WinNT_Compat dup /suspend get exec
Pscript_WinNT_Incr dup /resume get exec
/DeviceRGB dup setcolorspace /colspABC exch def
0 0 0 1 scol 2997 VM?
1 13356 49 <00010023fff20329028e001a005b00b800004558b800062f1bb900060009
3e59b800004558b8000d2f1bb9000d00093e59b800004558b800122f1bb9
001200053e59b800004558b800192f1bb9001900053e59ba000900120006
111239ba00150012000611123930313f021337273533171b013727353315
0f010323270b010717152323551f490a50ab38bf520a4fe0551f69373fc4
540a4fe01b1065016f65101a8ffe65019b65101a1a1065fdf39e01a8fe58
65101b00>/TTE1AD8890t00 AddT42Char 
2 4792 80 <00010055fff702fc01d70043008f00b800004558b800042f1bb900040007
3e59b800004558b8000c2f1bb9000c00073e59b800004558b800192f1bb9
001900073e59b800004558b800242f1bb9002400053e59b800004558b800
332f1bb9003300053e59b800004558b800432f1bb9004300053e59b80004
10b900010002fcba000600240004111239ba001400240004111239b8002c
d0b8003ad030313f012735370f013e01373e0133321615140e02073e0137
363332161514060f021715073f013e01353426232206070e010f01133e01
353426232206070e030f019115519e0d1f1a3b23213a161a270306070517
36203f2a1b2607053506509d0a3a03071113122d2233470c444305041211
132d211a2a21180945eaad0b1a1b588a304f2120202c24081a1f22102944
1d392e250e2a17c6350b1b1b50e40b260a141b252b41a358120134132008
111e252b21464c573212>/TTE1AD8890t00 AddT42Char 
1 2900 11 <00010048ff4301a602bd0012005bbb000e0003000300042b411b0006000e
0016000e0026000e0036000e0046000e0056000e0066000e0076000e0086
000e0096000e00a6000e00b6000e00c6000e000d5d410500d5000e00e500
0e00025d00b800002fb800082f3031172e0135343e0237170e0315141617
07e04d4b27517c5416486946222f3f1bbd44b56143938f85361e367e888d
4450a1451900>/TTE1AD8890t00 AddT42Char 
1 3056 12 <0001ffefff43014d02bc00110063bb000c0003000500042b410500da0005
00ea000500025d411b000900050019000500290005003900050049000500
5900050069000500790005008900050099000500a9000500b9000500c900
05000d5db8000c10b80013dc00b800092fb800112f3031073e0335342627
371e0115140e020711486946222f3f1a4d4c27517c55a0367f888d4450a0
461843b6604393908535>/TTE1AD8890t00 AddT42Char 
/TTE1AD8890t00 findfont /CharStrings get begin
/N 49 def
/m 80 def
/parenleft 11 def
/parenright 12 def
end
/TTE1AD8890t00 findfont /Encoding get
dup 24 /N put
dup 25 /m put
dup 26 /parenleft put
dup 27 /parenright put
pop
1344 2274 M <180D190B1A1B>[47 32 50 24 21  0]xS 
801 VM?
1 5848 25 <0002003dffeb01bc0289001a002f0190b800302fb800312fb8003010b800
03d0b800032fb8003110b80016dcba000e00030016111239b900200003fc
410500da002000ea002000025d411b000900200019002000290020003900
2000490020005900200069002000790020008900200099002000a9002000
b9002000c90020000d5db8000310b9002b0004fc41090096002b00a6002b
00b6002b00c6002b00045d41130006002b0016002b0026002b0036002b00
46002b0056002b0066002b0076002b0086002b00095d410500d5002b00e5
002b00025db80028d0b800282f00b800082fb800004558b800002f1bb900
0000053e59bb00110002002500042bba000e00000008111239b8000010b9
001b0001fc41210007001b0017001b0027001b0037001b0047001b005700
1b0067001b0077001b0087001b0097001b00a7001b00b7001b00c7001b00
d7001b00e7001b00f7001b00105d411b0007001b0017001b0027001b0037
001b0047001b0057001b0067001b0077001b0087001b0097001b00a7001b
00b7001b00c7001b000d71410500d6001b00e6001b000271303117222635
343e0237170e03073e0133321e0215140e0227323e0235342e0223220607
0e0115141e02f0555e2c567e5205285147370e1d4c25233a2a181e364b18
1a2a1f1112212e1c1a4117030212212f157f7655947148072308213e6148
201c1d33472b2f543e242a172a3a23243e2e1a181a141c14304d371e0000
>/TTE153FD90t00 AddT42Char 
/TTE153FD90t00 findfont /CharStrings get begin
/six 25 def
end
/TTE153FD90t00 findfont /Encoding get
dup 60 /six put
pop
F3S41 Ji 
1538 2274 M <051811050807290A3C19>[13 34 37 14 35 31 32 42 33  0]xS 
312 VM?
1 3674 26 <00010028ff3c01a301d9000c00464016010d0d400e090705040a09080704
0100090801010746762f3718003f3c2f012e2e2e2e2e2e002e2e2e313001
4968b90007000d496861b0405258381137b9000dffc0385917273f011323
0f013521150f01b2731f1b956a3e3e017b1d1bc42f3e3e017c02047c483e
3e00>/TTE16B5380t00 AddT42Char 
/TTE16B5380t00 findfont /CharStrings get begin
/seven 26 def
end
/TTE16B5380t00 findfont /Encoding get
dup 43 /seven put
pop
F1S41 Ji 
819 2391 M <011001101C102B>[34 18 34 18 36 18  0]xS 
1344 2391 M <0607280E0C0B080D0E07101709080607280E290D07171813081E0E0A0D082523>[18 35 32 35 31 32 26 17 36 35 18 33 34 25 19 34 32 36 33 18 35 33 41 35 26 35 35 25 18 25 33  0]xS 
2290 2391 M ($)S 
30000 VM?
Pscript_WinNT_Compat begin /$x mysetup def end
Pscript_WinNT_Incr dup /suspend get exec
Pscript_WinNT_Compat dup /initialize get exec
%%BeginDocument: Pscript_Win_PassThrough
813 2325/G1379104 FmND

%%EndDocument

Pscript_WinNT_Compat dup /suspend get exec
Pscript_WinNT_Incr dup /resume get exec
/DeviceRGB dup setcolorspace /colspABC exch def
0 0 0 1 scol F3S41 Ji 
1344 2507 M <3A18050B0A240A170711090506020711290A0419>[47 34 13 24 30 19 30 33 30 38 21 13 29 38 30 38 32 30 25  0]xS 
30000 VM?
Pscript_WinNT_Compat begin /$x mysetup def end
Pscript_WinNT_Incr dup /suspend get exec
Pscript_WinNT_Compat dup /initialize get exec
%%BeginDocument: Pscript_Win_PassThrough
1338 2442/G1385727 FmND

%%EndDocument

Pscript_WinNT_Compat dup /suspend get exec
Pscript_WinNT_Incr dup /resume get exec
/DeviceRGB dup setcolorspace /colspABC exch def
0 0 0 1 scol 312 VM?
1 3796 27 <00030026fff301c002b80015001f0029005a4023012a2a402b1325161106
2704041804131e040823040f131b070b2007000b0000010446762f371800
3f2f10fd10fd012f3cfd2ffd10fd2ffd2e2e002e2e3130014968b9000400
2a496861b0405258381137b9002affc03859172227263534372635343633
321716151407161514060336353426232206151413323635342706151416
ef5a3639746b715b573538727372544b2722212b3e222c4e4f2a0d2e3156
6b464c6050632d3055663d456e546901aa2d39232a28233afe972c253d30
2f3b262e>/TTE16B5380t00 AddT42Char 
/TTE16B5380t00 findfont /CharStrings get begin
/eight 27 def
end
/TTE16B5380t00 findfont /Encoding get
dup 44 /eight put
pop
F1S41 Ji 
819 2624 M <011001101C102C>[34 18 34 18 36 17  0]xS 
1344 2624 M <04>S 
1379 2624 M <09170D0F080A2510170908061F2324050B0712050409170D0F080A2510170908061F0F23>[34 33 18 27 26 24 30 18 32 34 26 18 45 25 26 16 32 35 36 16 35 34 33 18 28 25 25 29 19 32 34 25
19 44 28  0]xS 
2383 2624 M ($)S 
30000 VM?
Pscript_WinNT_Compat begin /$x mysetup def end
Pscript_WinNT_Incr dup /suspend get exec
Pscript_WinNT_Compat dup /initialize get exec
%%BeginDocument: Pscript_Win_PassThrough
813 2558/G1379106 FmND

%%EndDocument

Pscript_WinNT_Compat dup /suspend get exec
Pscript_WinNT_Incr dup /resume get exec
/DeviceRGB dup setcolorspace /colspABC exch def
0 0 0 1 scol F3S41 Ji 
1344 2741 M <3A18050B0A240A170711090506020711290A0419>[47 34 13 24 30 19 30 33 30 38 21 13 29 38 30 38 32 30 25  0]xS 
30000 VM?
Pscript_WinNT_Compat begin /$x mysetup def end
Pscript_WinNT_Incr dup /suspend get exec
Pscript_WinNT_Compat dup /initialize get exec
%%BeginDocument: Pscript_Win_PassThrough
1338 2675/G1385730 FmND

%%EndDocument

Pscript_WinNT_Compat dup /suspend get exec
Pscript_WinNT_Incr dup /resume get exec
/DeviceRGB dup setcolorspace /colspABC exch def
0 0 0 1 scol 312 VM?
1 4010 28 <00020026ff3401c901e40011001e004c401b011f1f4020040909071c0404
15040f18070b120600060001010f46762f3718003f2f10fd2ffd012ffd2f
fd2e2e002e3130014968b9000f001f496861b0405258381137b9001fffc0
385901321716151005273637062322272635343617220615141633323736
35342601005a3639fe9a24cd2c303d4a2c2f785724302e253025053001e4
42467afeaa586c3180253537616b80733c342f3b231713404d00>/TTE16B5380t00 AddT42Char 
/TTE16B5380t00 findfont /CharStrings get begin
/nine 28 def
end
/TTE16B5380t00 findfont /Encoding get
dup 45 /nine put
pop
F1S41 Ji 
819 2857 M <011001101C102D>[34 18 34 18 36 18  0]xS 
1344 2857 M <04>S 
312 VM?
1 2080 15 <00010015ff7000bf008e000f0040401401101040110308060d040300050a
000800010646762f3718003f3f2f2f012ffd2e2e003130014968b9000600
10496861b0405258381137b90010ffc03859373216151407273637062322
2635343671222c8e1c470f05091a222e8e362e8d2d47152f032920202d00
>/TTE16B5380t00 AddT42Char 
/TTE16B5380t00 findfont /CharStrings get begin
/comma 15 def
end
/TTE16B5380t00 findfont /Encoding get
dup 46 /comma put
pop
1379 2857 M <09170D0F080A25101709080409170D0F080A2523242E050409170D0F080A251017090803090A28090A23242E050409170D0F080A25100A09170D0F08090A23242E05>
[34 33 18 27 26 24 30 18 32 34 26 35 34 33 18 27 26 24 33 26 25 19 16 35 34 33 18 27 26 25 29 18
33 34 25 35 34 25 32 34 24 26 25 18 17 35 34 33 17 28 26 24 30 18 24 34 33 18 27 26 34 24 26 25
18  0]xS 
30000 VM?
Pscript_WinNT_Compat begin /$x mysetup def end
Pscript_WinNT_Incr dup /suspend get exec
Pscript_WinNT_Compat dup /initialize get exec
%%BeginDocument: Pscript_Win_PassThrough
813 2792/G1379108 FmND

%%EndDocument

Pscript_WinNT_Compat dup /suspend get exec
Pscript_WinNT_Incr dup /resume get exec
/DeviceRGB dup setcolorspace /colspABC exch def
0 0 0 1 scol 1344 2937 M <0409170D0F080A251013070A09170D0F08090A23>[35 34 33 18 27 26 24 30 17 35 35 24 34 33 18 28 25 34 25  0]xS 
1904 2937 M ($)S 
F3S41 Ji 
1344 3054 M <3A18050B0A240A170711090506020711290A0419>[47 34 13 24 30 19 30 33 30 38 21 13 29 38 30 38 32 30 25  0]xS 
30000 VM?
Pscript_WinNT_Compat begin /$x mysetup def end
Pscript_WinNT_Incr dup /suspend get exec
Pscript_WinNT_Compat dup /initialize get exec
%%BeginDocument: Pscript_Win_PassThrough
1338 2989/G1385733 FmND

%%EndDocument

Pscript_WinNT_Compat dup /suspend get exec
Pscript_WinNT_Incr dup /resume get exec
/DeviceRGB dup setcolorspace /colspABC exch def
0 0 0 1 scol F1S83 Ji 
819 3269 M <01101B>[65 34  0]xS 
1344 3269 M <04>S 
1414 3269 M <091A090A09070C090F>[65 42 65 47 65 67 58 65  0]xS 
30000 VM?
Pscript_WinNT_Compat begin /$x mysetup def end
Pscript_WinNT_Incr dup /suspend get exec
Pscript_WinNT_Compat dup /initialize get exec
%%BeginDocument: Pscript_Win_PassThrough
813 3138/G1379111 FmND

%%EndDocument

Pscript_WinNT_Compat dup /suspend get exec
Pscript_WinNT_Incr dup /resume get exec
/DeviceRGB dup setcolorspace /colspABC exch def
0 0 0 1 scol F3S41 Ji 
1183 3430 M <1A1B1C>[16 33  0]xS 
F4S41 Ji 
1344 3430 M <01020304050606040708090A0B090A040C0D090E0F0B1004111213>[20 33 39 13 33 33 32 13 42 28 32 19 24 33 18 13 48 32 33 32 17 24 25 13 45 34  0]xS 
30000 VM?
Pscript_WinNT_Compat begin /$x mysetup def end
Pscript_WinNT_Incr dup /suspend get exec
Pscript_WinNT_Compat dup /initialize get exec
%%BeginDocument: Pscript_Win_PassThrough
813 3364/G1379112 FmND

%%EndDocument

Pscript_WinNT_Compat dup /suspend get exec
Pscript_WinNT_Incr dup /resume get exec
/DeviceRGB dup setcolorspace /colspABC exch def
0 0 0 1 scol 2403 VM?
1 3624 18 <00010014fff6011702a20003002500b800004558b800012f1bb900010009
3e59b800004558b800032f1bb9000300053e5930313713170314da29da03
029f0efd62000000>/TTE153FD90t00 AddT42Char 
1 8556 34 <0002001cfff8016e02c1001e002a012abb00280004002200042bbb001700
03000800042b411b00060028001600280026002800360028004600280056
00280066002800760028008600280096002800a6002800b6002800c60028
000d5d410500d5002800e5002800025dba000000220028111239b800002f
410500da000800ea000800025d411b000900080019000800290008003900
0800490008005900080069000800790008008900080099000800a9000800
b9000800c90008000d5db9001d0003fcb8001710b8002cdc00b800004558
b8001f2f1bb9001f00053e59bb00120002000b00042bb8001f10b9002500
02fc411b0007002500170025002700250037002500470025005700250067
002500770025008700250097002500a7002500b7002500c70025000d5d41
0500d6002500e6002500025d303137343e02373e0135342623220607273e
0133321e02151406070e011523172226353436333216151406a30d151a0e
1d2b463f1f411c18225e30223b2c1930242629281615201d1814201cbd27
3c2e240f1d452d36431b1b18282e16293822395a22235b38c51a1a14211e
17132100>/TTE153FD90t00 AddT42Char 
1 8420 32 <0002002b008401c9013700030007001700bb00050001000600042bbb0001
0001000200042b303113211521152115212b019efe62019efe6201372961
29000000>/TTE153FD90t00 AddT42Char 
/TTE153FD90t00 findfont /CharStrings get begin
/slash 18 def
/question 34 def
/equal 32 def
end
/TTE153FD90t00 findfont /Encoding get
dup 61 /slash put
dup 62 /question put
dup 63 /equal put
pop
F3S41 Ji 
1344 3510 M <02090908353D3D101010193B060819180B293D0A113D3B040B3D0C0A090703243E030C3F2A1B1B>[38 22 21 36 13 20 20 49 50 50 13 17 29 36 13 34 24 32 20 29 38 19 18 25 24 19 35 29 22 30 19 20
26 19 35 32 33 33  0]xS 
30000 VM?
Pscript_WinNT_Compat begin /$x mysetup def end
Pscript_WinNT_Incr dup /suspend get exec
Pscript_WinNT_Compat dup /initialize get exec
%%BeginDocument: Pscript_Win_PassThrough
1338 3444/G1379113 FmND
1338 3561/G1379034 FmND
1338 3684/G1238624 FmND
; ; 
%%EndDocument

Pscript_WinNT_Compat dup /suspend get exec
Pscript_WinNT_Incr dup /resume get exec
LH
(%%[Page: 3]%%) = 
%%PageTrailer

%%EndPageComments
%%BeginPageSetup
/DeviceRGB dup setcolorspace /colspABC exch def
mysetup concat colspRefresh
%%EndPageSetup

30000 VM?
Pscript_WinNT_Compat begin /$x mysetup def end
Pscript_WinNT_Incr dup /suspend get exec
Pscript_WinNT_Compat dup /initialize get exec
%%BeginDocument: Pscript_Win_PassThrough
% Copyright (c) 1986-1995 Frame Technology Corporation.
% DO NOT CHANGE THE FIRST LINE; Adobe PS 5.1.2 depends on it.
/FMcmyk 100 dict def
/FmBD{bind def}bind def
/FmLD{load def}FmBD
/FMc {
 FMcmyk length FMcmyk maxlength ge { /FMcmyk FMcmyk dup length dup add dict copy def } if
 4 array astore 4 1 roll 8 bitshift add 8 bitshift add exch FMcmyk 3 1 roll put
}FmBD
/setcmykcolor where { pop
 /sc where { pop /sc load 0 get /scignore eq {
  /FMsc /sc FmLD
  /sc { 3 copy 8 bitshift add 8 bitshift add FMcmyk 1 index known
   { FMcmyk exch get aload pop setcmykcolor pop pop pop } { pop FMsc } ifelse
  }FmBD
 } if } if
} if
mark { /S load
 dup 0 get /PenW eq { dup 1 get /sl load eq {
  dup 0 { PenW .75 sub sl } bind put 1 /exec load put
 } if } if
} stopped cleartomark       
/FmX matrix defaultmatrix def
/FmDC {transform FmX defaultmatrix itransform cvi exch cvi exch} def
/FmBx { dup 3 index lt {3 1 roll exch} if 
 1 index 4 index lt {4 -1 roll 3 1 roll exch 4 1 roll} if
}FmBD
/FmPD/cleartomark FmLD
/FmPD2/cleartomark FmLD
/FmPD4/cleartomark FmLD
/FmPT/pop FmLD
/FmPA{pop pop pop}FmBD
/FmND{pop pop pop}FmBD
systemdict /pdfmark known systemdict /currentdistillerparams known and {
  /FmPD/pdfmark FmLD
  currentdistillerparams /CoreDistVersion get 2000 ge {
    /FmPD2/pdfmark FmLD
    % FmPD4 is like FmPD but for Acrobat 4.05-specific PDF
    /FmPD4U true def
    currentdistillerparams /CoreDistVersion get 4050 ge 
    {
      /FmPD4 /pdfmark load def
    }
    {
      /FmPD4
      { FmPD4U 
	{(%%[Acrobat Distiller 4.05 and up is required to generate Logical PDF Structure]%%) = 
	  (%%[Logical PDF Structure is not generated.]%%) = flush
	} if
	/FmPD4U false def
	cleartomark
      } FmBD
    } ifelse


% Procedure FmPA used to define named destinations for all paragraphs (and elements). As
% a result, the produced PDF files were very large in size, because they contained numerous
% named destinations that were not actually used. 
% In FrameMaker 5.5.6 FmND procedure was added to allow to distinguish between 
% named destinations that were definitely used and all other named destinations.
% The users were given opportunity to change the definition of the FmPA procedure,
% so that it either directed Distiller to produce or to not produce named destinations
% (under fmCG switch).
% FmND always produced a named destination, but FmPA produced a named destination onlY
% when fmCG switch was set to 'True'.
% FrameMaker 5.5.6 used very simplistic method to determine whether a named destination
% was actually used or not.
% FrameMaker 6.0 and up uses a much more elaborate method to determine whether a 
% named destination is actually used or not. It also has an improved "Acrobat Setup" dialog,
% which allows to specify the user's preference, whether to create PDF files with named
% destinations for all paragraphs, or Wonly those named destinations that are used.
% Therefore, there is no need for fmCG switch in FrameMaker 6.0 and up. Now FmND procedure
% defines a named destination, and FmPA procedure does nothing. Sophisticated users still 
% have ability to modify definition of FmPA if they wish so.

	  /fmCG true def 

	  /FmND
	  { mark exch /Dest exch 5 3 roll /View [ /XYZ 5 -2 roll FmDC null ] /DEST FmPD 
	  }FmBD

	  /FmPA 
	  { fmCG
	    { pop pop pop }
	    { FmND } ifelse
	  } FmBD
 } if
} if
: N : N [/CropBox[469 5266 FmDC 3776 305 FmDC FmBx]/PAGE FmPD
[/Dest/P.8/DEST FmPD2
[/Dest/L/DEST FmPD2
: N ; [/Rect[904 5088 1007 5009]/Border[0 0 0]/Dest/M21.9.last.page/Action/GoToR/File(meg/book.pdf)/LNK FmPD2
: N [/Title(A)/Rect[779 4965 3492 633]/ARTICLE FmPD2
; ; : N : N 
%%EndDocument

Pscript_WinNT_Compat dup /suspend get exec
Pscript_WinNT_Incr dup /resume get exec
/DeviceRGB dup setcolorspace /colspABC exch def
0 0 0 1 scol F0S48 Ji 
845 550 M <11>S 
879 550 M <060E0607061909061205>[36 21 36 24 36 37 31 36 29  0]xS 
2615 550 M <1702110518190D06070C090D040F1912051A060712040F19051B1C16>[18 38 36 17 18 38 25 36 24 34 32 25 18 38 38 29 17 37 35 25 29 18 38 37 17 33 18  0]xS 
317 VM?
1 3880 27 <00030035fff6019f02a40014001e002900584022012a2a402b0e24151006
1d0408260404172204120e1a060b1f06000b020000010446762f3718003f
3f10fd10fd012f3cfd3c2ffd2ffd2e2e002e2e3130014968b90004002a49
6861b0405258381137b9002affc038591722272635343726353436333216
15140716151406033635342623220615141332363534270615141716ea51
30347f6f5e504d5f7878624c6735302d3a5e2f3e6d6e1c1e0a2b2e516151
53544a61584f5a50515c4c640183354b2f3b372f49fe833930513d394f32
1d20>/TTE152A528t00 AddT42Char 
/TTE152A528t00 findfont /CharStrings get begin
/eight 27 def
end
/TTE152A528t00 findfont /Encoding get
dup 37 /eight put
pop
F0S41 Ji 
845 5068 M <2515141616>[34 25 32 35  0]xS 
2565 5068 M <010203040502060708040906050A0B0C0D0E0F07100511060B060C12060513>[47 33 43 16 15 34 32 22 30 16 28 33 15 31 16 30 23 20 34 22 51 15 33 32 16 32 31 26 32 16  0]xS 
N 3419 4989 M 3421 4989 I 3421 4985 I 3419 4985 I C 
 L N 1362 4989 M 1360 4989 I 1360 4985 I 1362 4985 I C 
 L N 3419 4989 M 3419 4985 I 1362 4985 I 1362 4989 I C 
 L N 1318 4989 M 1320 4989 I 1320 4985 I 1318 4985 I C 
 L N 837 4989 M 835 4989 I 835 4985 I 837 4985 I C 
 L N 1318 4989 M 1318 4985 I 837 4985 I 837 4989 I C 
 L N 3423 613 M 3425 613 I 3425 609 I 3423 609 I C 
 L N 1367 613 M 1365 613 I 1365 609 I 1367 609 I C 
 L N 3423 613 M 3423 609 I 1367 609 I 1367 613 I C 
 L N 1323 613 M 1325 613 I 1325 609 I 1323 609 I C 
 L N 842 613 M 840 613 I 840 609 I 842 609 I C 
 L N 1323 613 M 1323 609 I 842 609 I 842 613 I C 
 L ; ; ; LH
(%%[Page: 4]%%) = 
%%PageTrailer

%%Trailer
%%DocumentNeededResources: 
%%DocumentSuppliedResources: 
%%+ procset Pscript_WinNT_ErrorHandler 5.0 0
%%+ procset Pscript_FatalError 5.0 0
%%+ procset Pscript_Win_Basic 5.0 0
%%+ procset Pscript_Win_Utils_L2 5.0 0
%%+ procset Pscript_WinNT_Compat 5.0 0
%%+ procset Pscript_T42Hdr 5.0 0
%%+ procset Pscript_Text 5.0 0
%%+ procset Pscript_Win_GdiObject 5.0 0
%%+ procset Pscript_Win_GdiObject_L3 5.0 0
Pscript_WinNT_Incr dup /terminate get exec
ehsave restore
(%%[LastPage]%%) = 
%%EOF
