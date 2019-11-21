#version 150
in vec2 inPosition;

uniform mat4 view;
uniform mat4 projection;
uniform float time;


uniform float type;
uniform float teleso;

uniform float pi = 3.14159265359;

float getZ(vec2 vec){
	return sin(time + vec.y*pi);
}

vec3 getSphere(vec2 vec){
	float az = vec.x * pi;
	float ze = vec.y * pi/2;
	float r = 1;

	float x = r * cos(az) * cos(ze);
	float y = r * sin(az) * cos(ze);
	float z = r * sin(ze);
	return vec3(x, y, z);
}


vec3 getWTF(vec2 vec){
	float x = vec.x;
	float y = vec.y;
	float z = sin(time+x);
	return vec3(x, y, z);
}

vec3 getWeirdSphere(vec2 vec){
	float az = vec.x * pi;
	float ze = vec.y * pi/2;
	float r = 1;

	float x = r * cos(az) * cos(ze);
	float y = 2*  r * sin(az) * cos(ze);
	float z = r * sin(ze);
	return vec3(x, y, z);
}
vec3 getBumpySphere(vec2 vec){
	float s = vec.x * pi/2;
	float t = vec.y * pi;
	float rho = 1+0.2*sin(6*s)*sin(5*t);
	float phi = t;
	float theta = s;

	float x = rho * cos(phi) * cos(theta);
	float y =    rho * sin(phi) * cos(theta);
	float z =       rho * sin(theta);
	return vec3(x, y, z);
}
vec3 getParsur(vec2 vec){
	float s = pi*vec.x;
	float t = vec.y;
	float x = t* cos(s);
	float y =  t * sin(s);
	float z =t;
	return vec3(x, y, z);
}

vec3 getCylinder(vec2 vec){
	float s = pi*vec.x;
	float t = vec.y;
	float r = 1;
	float theta =  s;
	float z = t;
	return vec3(r*cos(theta), r*sin(theta), z);
}
vec3 getTrophy(vec2 vec) {
	float s = pi*vec.x;
	float t = pi*vec.y;
	float r = (1-max(sin(t),0))*2;
	float theta =  -s;
	float z = 3-t;
	return vec3(r*cos(theta)/3, r*sin(theta)/3, z/3-1);
}


vec3 getPlane(vec2 vec){
	return vec3(vec*4, -1);
}
vec3 getMovingPlane(vec2 vec){
	return vec3(vec*cos(vec.x), getZ(vec));
}



void main() {
	vec2 pos = inPosition *2-1;
	vec4 pos4;

	if (type == 1.0){

		if (teleso==1.0){
			pos4 =  vec4(getParsur(pos), 1.0);
		}
		else if (teleso == 0.0){
			pos4 = vec4(getWeirdSphere(pos), 1.0);
		}
		else if (teleso == 2.0){
			pos4 = vec4(getMovingPlane(pos), 1.0);
		}
		else if (teleso == 3.0){
			pos4 = vec4(getCylinder(pos), 1.0);
		}
		else if (teleso == 4.0){
			pos4 = vec4(getTrophy(pos), 1.0);
		}
		else if (teleso == 5.0){
			pos4 = vec4(getBumpySphere(pos), 1.0);
		}
	} else if (type==0){
		pos4 = vec4(getPlane(pos), 1);

	}

	gl_Position = projection * view * pos4;
}
