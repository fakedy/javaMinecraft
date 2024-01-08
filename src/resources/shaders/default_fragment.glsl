#version 330 core
out vec4 FragColor;

in vec2 TexCoord;
in vec3 Normal;
in vec3 FragPos;
in vec4 FragPosLightSpace;

uniform sampler2D ourTexture;
uniform sampler2D shadowMap;
uniform vec3 lightPos;

uniform vec3 plyPos;


vec3 lightDir;
vec3 normal;

float fragDist = pow((pow((plyPos.x - FragPos.x), 2) + pow((plyPos.y - FragPos.y), 2) + pow((plyPos.z - FragPos.z), 2)), 0.5); // calculates distance between camera and frag position. seems like glsl already have a dist func lol.

vec3 lightDirection = vec3(-0.5f, -1.0f, -0.5f);

float get_fog_factor() {

    float nearplane = 180.0; // Where the fog starts
    float farplane =  250.0; // where the max fog is reached

    float fogmax = 1.0 * farplane;
    float fogmin = 0.5 * farplane;
    //if (fragDist >= fogmax)discard; // fog edge
    //if (fragDist <= fogmin)return 0.0; // fog edge
    fragDist = clamp(fragDist, fogmin, fogmax);


    return 1.0 - (fogmax - fragDist) / (fogmax - fogmin); // everything between the fog edges
}

float ShadowCalculation(vec4 fragPosLightSpace)
{
    // perform perspective divide
    vec3 projCoords = fragPosLightSpace.xyz / fragPosLightSpace.w;
    // transform to [0,1] range
    projCoords = projCoords * 0.5 + 0.5;
    // get closest depth value from light's perspective (using [0,1] range fragPosLight as coords)
    float closestDepth = texture(shadowMap, projCoords.xy).r;
    // get depth of current fragment from light's perspective
    float currentDepth = projCoords.z;
    // check whether current frag pos is in shadow

    float bias = max(0.05 * (1.0 - dot(normal, lightDir)), 0.005);
    float shadow = currentDepth -  bias > closestDepth  ? 1.0 : 0.0;

    if(projCoords.z > 1.0)
    shadow = 0.0;


    return 0.0;
    //return shadow;
}

void main()
{
    vec3 color = texture(ourTexture, TexCoord).rgb;
    normal = normalize(Normal);
    vec3 lightColor = vec3(1.0, 0.95, 0.8);
    // ambient
    vec3 ambient = 0.2 * lightColor; // indirect light
    // diffuse
    lightDir = normalize(lightPos - FragPos);
    float diff = max(dot(lightDir, normal), 0.0);
    vec3 diffuse = diff * lightColor;
    // specular
    vec3 viewDir = normalize(plyPos - FragPos);
    vec3 reflectDir = reflect(-lightDir, normal);
    vec3 halfwayDir = normalize(lightDir + viewDir);
    float spec = 0.0;
    float shininess = 256.0;
    //spec = pow(max(dot(normal, halfwayDir), 0.0), shininess);
    vec3 specular = spec * lightColor;
    // calculate shadow
    float shadow = ShadowCalculation(FragPosLightSpace);
    vec3 lighting = (ambient +    (1.0 - shadow)* (diffuse + specular)) * color;
    //float gamma = 2.2;
    //lighting.rgb = pow(lighting, vec3(1.0/gamma));
    float fogFactor = get_fog_factor();

    FragColor = mix(vec4(lighting,1.0f), vec4(lightColor, 0.0), fogFactor);
}