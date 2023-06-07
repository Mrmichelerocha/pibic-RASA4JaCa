!inicializa.

+request(RequestedBy, ResponseId, IntentName, Params, Contexts)
	: user(ResponseId) //se for meu usuário trato a requisição
<-
	.print("Request received ",IntentName," of Rasa");
	!responder(RequestedBy, ResponseId, IntentName, Params, Contexts);
	.

+!responder(RequestedBy, ResponseId, IntentName, Params, Contexts)
	: (IntentName == "inform_stress_other") & user(Name)
<-
	.nth(1,Params,param("name",X));
	.concat("Você informou que a ",X," está estressada.",B);
	+informou_stress(Name,X);
	.findall(N,informou_stress(_,N),L);
	.print("Lista que a ", Name ," informou ", L);
	reply(B);
	.

+!responder(RequestedBy, ResponseId, IntentName, Params, Contexts)
	: (IntentName == "inform_self_stress") & user(Name)
<-
	+stressed(Name);
	.print("Informou self stress!")
	reply("Você informou que está estressada.");
	.

+!responder(RequestedBy, ResponseId, IntentName, Params, Contexts)
	: true
<-
	reply("Sorry, I do not recognize this intention");
	.

+!inicializa 
	<- 	joinWorkspace("wp",WP1); //se junta ao workspace do artefato do Rasa4Jaca
		lookupArtifact("dial4JaCa",DJ); // descobre o ID do artefato chamado de dial4JaCa 
		focus(DJ);  // dá foco nele
		.print("==>> Assitente Iniciado").

{ include("$jacamoJar/templates/common-cartago.asl") }
{ include("$jacamoJar/templates/common-moise.asl") }
