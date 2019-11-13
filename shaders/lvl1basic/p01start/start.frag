#version 150
out vec4 outColor; // output from the fragment shader
in vec3 vertColor;
in vec3 normal;
in vec3 light;
in vec3 viewDirection;
in vec4 depthTextureCoord;
in vec2 texCoord;
uniform float type;
uniform sampler2D textureMosaic;

uniform sampler2D depthTexture;
void main() {
	vec4 ambient = vec4(vec3(0.15),1); //barva ambientu
	float NdotL = max(0,dot(normalize(normal),normalize(light)));
	vec4 diffuse = vec4(NdotL*vec3(0.35),1); //barva difusního
	vec3 halfVector = normalize(normalize(light)+normalize(viewDirection));
	float NdotH = dot(normalize(normal), halfVector);
	vec4 specular = vec4(pow(NdotH,16) * vec3(1),1);

	vec4 finalColor = diffuse + ambient + specular ;
		vec4 textureColor = texture(textureMosaic, texCoord);
	float zL = texture(depthTexture, depthTextureCoord.xy).r; // R G i B složky jsou stejné, protože gl_fragcoord.zzz

	float zA = depthTextureCoord.z;

	bool shadow = zL < zA - 0.005;

	if (shadow){
		outColor = ambient*textureColor;
		//outColor = diffuse + ambient + specular ;

	} else {
		//outColor = vec4(0,0,1,1);
		     //outColor = depthTextureCoord + diffuse + ambient + specular ;
		outColor = finalColor*textureColor;
	}
	if (type == 2.0){
		outColor = vec4(1,1,0,1);
	}


}
