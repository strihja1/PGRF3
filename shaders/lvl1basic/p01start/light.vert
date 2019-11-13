#version 150
in vec2 inPosition; // input from the vertex buffer

uniform mat4 view;
uniform mat4 projection;
uniform float time;   // in je pro každý vrchol jiný, uniform je stejný


uniform float type;
uniform float teleso;

float getZ(vec2 vec){
	return sin(time + vec.y*3.14);
}

vec3 getSphere(vec2 vec){
	float az = vec.x * 3.14f;
	float ze = vec.y * 3.14f/2;
	float r = 1;

	float x = r * cos(az) * cos(ze);
	float y =    r * sin(az) * cos(ze);
	float z = r * sin(ze);
	return vec3(x, y, z);
}


vec3 getWeirdSphere(vec2 vec){
	float az = vec.x * 3.14f;
	float ze = vec.y * 3.14f/2;
	float r = 1;

	float x = r * cos(az) * cos(ze);
	float y =  2*  r * sin(az) * cos(ze);
	float z =       r * sin(ze);
	return vec3(x, y, z);
}

vec3 getWTF(vec2 vec){
	/*	float az = vec.x * 3.14f;
	float ze = vec.y * 3.14f/2;
	float r = 1;

	float x = r * cos(az) * cos(ze);
	float y = r * sin(az) * cos(ze);
	float z = sin(time+vec.y);
	return vec3(x, y, z);*/

	float x = vec.x;
	float y = vec.y;
	float z = sin(time+x);
	return vec3(x,y,z);
}

vec3 getPlane(vec2 vec){
	return vec3(vec*4,-1);
}




void main() {
	vec2 pos = inPosition *2-1; // je od -1 do +1
	vec4 pos4;

	//vec4 pos4 = vec4(pos, getZ(pos), 1.0);
	/*if(type == 1.0){
		pos4 = vec4(getSphere(pos), 1.0);


	}else{
		pos4 = vec4(getPlane(pos),1);

	}*/
	if(type == 1.0){
		if(teleso==1.0){
			pos4 = vec4(getWTF(pos), 1.0);
		}
		else{
		pos4 = vec4(getWeirdSphere(pos), 1.0);
		}


	}else if (type==0) {
		pos4 = vec4(getPlane(pos),1);

	}else if (type==2.0){
//pos4 = vec4(getSphere(pos),1);

}

	gl_Position = projection * view * pos4;
}
