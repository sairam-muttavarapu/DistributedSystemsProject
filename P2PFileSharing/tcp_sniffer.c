#include <unistd.h>
#include<stdio.h> 
#include<stdlib.h>   
#include<string.h> 
#include<netinet/ip_icmp.h>
#include<netinet/udp.h>
#include<netinet/tcp.h>
#include<netinet/ip.h>
#include<sys/socket.h>
#include<arpa/inet.h>
 
#define PACKET_SIZE 65536
void tcpPacket(unsigned char* , int);
void packetProcess(unsigned char* , int);
void ipHeader(unsigned char* , int, char*);
 
FILE *tcp_fp;
int sock;
int syn_flag =0,ack_flag=0;
unsigned long int syn_flood_counter =0,syn_flood_rand_counter=0,ack_flood_counter=0;
struct sockaddr_in source,dest;
int tcpCounter=0,igmpCounter=0,icmpCounter=0,udpCounter=0,othersCounter=0,totalPacketCount=0,i,j;
 
int main()
{
    struct sockaddr socketAddr;
    int socketSize ,n;
   
    unsigned char *buffer = (unsigned char *)malloc(PACKET_SIZE); //Its Big!
    daemon(1,0);    
    tcp_fp = fopen("tcp_security_logs","w");
    if(tcp_fp==NULL) printf("Unable to create tcp log file.");
    printf("Starting to sniff packets...\n");
    sock = socket(AF_INET , SOCK_RAW , IPPROTO_TCP);   // sniff tcp packets
    if(sock < 0){
        printf("Socket Error\n");
        exit(1);
       }
    while(1){
        socketSize = sizeof(socketAddr);
        n = recvfrom(sock , buffer , PACKET_SIZE , 0 , &socketAddr , &socketSize);
        if(n  <0 ){
          printf("recvfrom ERROR! nothing received!\n");
          exit(1);
          }
        packetProcess(buffer , n);
        }
    close(sock);
    fclose(tcp_fp);
    return 0;
}
 
void packetProcess(unsigned char* buffer, int size){
    struct iphdr *iph = (struct iphdr*)buffer;
    unsigned short iphdrlen;
    struct tcphdr *tcph;
    totalPacketCount++;

    if (iph->protocol == 1)
      icmpCounter++;
    else if ( iph->protocol == 2)
      igmpCounter++;
    else if ( iph->protocol  == 6){
      iphdrlen = iph->ihl*4;
      tcph=(struct tcphdr*)(buffer + iphdrlen);
      if ( ntohs(tcph->dest) == 4003){
          tcpCounter++;
          tcpPacket(buffer , size);
          }
      }
     else if ( iph->protocol  == 17)  
       udpCounter++;
     else //arp, etc
       othersCounter++;
    
    printf("TCP : %d   UDP : %d   ICMP : %d   IGMP : %d   Others : %d   Total : %d\r",tcpCounter,udpCounter,icmpCounter,
                                                                                      igmpCounter,othersCounter,totalPacketCount);
}
 
void ipHeader(unsigned char* Buffer, int Size,char *temp)
{
    unsigned short iphdrlen;
         
    struct iphdr *iph = (struct iphdr *)Buffer;
    iphdrlen =iph->ihl*4;
     
    memset(&source, 0, sizeof(source));
    source.sin_addr.s_addr = iph->saddr;
     
    memset(&dest, 0, sizeof(dest));
    dest.sin_addr.s_addr = iph->daddr;
     
    sprintf(temp,"source IP:%s\n",inet_ntoa(source.sin_addr));
}
 
void tcpPacket(unsigned char* Buffer, int Size)
{
    unsigned short iphdrlen;
    struct iphdr *iph = (struct iphdr *)Buffer;
    iphdrlen = iph->ihl*4;
    char log_buffer[500],temp[100];
    struct tcphdr *tcph=(struct tcphdr*)(Buffer + iphdrlen);
             
         
    ipHeader(Buffer,Size,temp);
      
    if ( syn_flag){
      if (tcph->ack == 0){
        if ( tcph->syn == 1){
          syn_flood_rand_counter++;
          //printf("oops rand!  %ld\n",syn_flood_rand_counter);
          }
        else{
	  syn_flood_counter++;
          //printf("oops %ld\n",syn_flood_counter);
          }
       }
     
      }
    else if ( ack_flag){
      if ( (tcph->urg == 0) && (tcph->ack == 1) && (tcph->psh == 0) && (tcph->rst == 0) && (tcph->syn == 0) && (tcph->fin == 0)){
         ack_flood_counter++;
       }
      else{
         ack_flood_counter = 0; // why ? look for continous ack floods. There are other normal ones also, remember!
       }
    }
    
    
    sprintf(log_buffer,"%ssingle SYN_FLOOD index:%ld\nRandom SYN_FLOOD index:%ld\nACK_FLOOD index:%ld\n",temp,syn_flood_counter,syn_flood_rand_counter,ack_flood_counter);
    syn_flag =0;
    ack_flag =0;
    // check if it's syn packet   
    if ( (tcph->urg == 0) && (tcph->ack == 0) && (tcph->psh == 0) && (tcph->rst == 0) && (tcph->syn == 1) && (tcph->fin == 0)){
      //syn_counter++;
      syn_flag =1;
      }
     // check if it's ack packet
    
    else if ( (tcph->urg == 0) && (tcph->ack == 1) && (tcph->psh == 0) && (tcph->rst == 0) && (tcph->syn == 0) && (tcph->fin == 0)){
      //syn_counter++;
      ack_flag =1;
      }
                          
    rewind(tcp_fp);
    fprintf(tcp_fp,"%s",log_buffer);
}
 
